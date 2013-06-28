/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.particles;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;
import com.jme3.util.SafeArrayList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * The ParticleController is the core class that ties together all the sections
 * of the plugable particle system.
 *
 * @author Tim Boura - Zero Separation Ltd
 */
public class ParticleController extends AbstractControl {

    private String name;
    private ParticleMesh mesh;
    private ParticleSource source;
    private ParticleEmissionController emissionController;
    private SafeArrayList<ParticleInfluencer> influencers;
    private ParticleData[] particles;
    private float lifeMin;
    private float lifeMax;
    private float lifeDiff;
    private int nextIndex = 0;
    private Geometry geometry = null;
    private Camera camera;
    
    private int activeCount=0;

    /**
     * Constructs a new ParticleController with the given name (optional) and
     * the behaviour as specified by the passed parameters.
     *
     * One the ParticleController has been constructed you can query the
     * geometry using getGeometry() and attach the geometry to the scene graph
     * in any suitable location. Transforming that geometry will move all of the
     * particles but all ParticleSource and ParticleInfluencers will continue to
     * operate as would be expected.
     *
     * Removing the geometry from the scene graph will pause the particles and
     * the ParticleController and it will consume no resources (other than
     * memory!). To activate it again simply re-attach it.
     *
     * @param name The name to use for the geometry in the scene graph
     * @param mesh The mesh to use (Usually either PointMesh or QuadMesh)
     * @param maxParticles The maximum number of particles to allow active at
     * any one time
     * @param lifeMin The minimum amount of time (in seconds) for which each particle lives
     * @param lifeMax The maximum amount of time (in seconds) for which each particle lives
     * @param source The source from which the particles are spawned
     * @param emissionController The frequency and timing with which particles are
     * spawned. If null then no particles are automatically spawned and they
     * must be triggered manually using emitNextParticle() or emitAllParticles()
     * @param influencers Zero or more ParticleInfluencers, each of which
     * changes the behaviour of the particles.
     */
    public ParticleController(
            String name,
            ParticleMesh mesh,
            int maxParticles,
            float lifeMin,
            float lifeMax,
            ParticleSource source,
            ParticleEmissionController emissionController,
            ParticleInfluencer... influencers) {
        this.name = name;
        this.mesh = mesh;
        this.source = source;
        setEmissionController(emissionController);
        this.influencers = new SafeArrayList<ParticleInfluencer>(ParticleInfluencer.class);
        this.influencers.addAll(Arrays.asList(influencers));
        particles = new ParticleData[maxParticles];
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new ParticleData();
        }
        this.lifeMin = lifeMin;
        this.lifeMax = lifeMax;
        this.lifeDiff = lifeMax - lifeMin;
    }
    
    /**
     * Used internally by cloneForSpatial. It clones all passed objects
     */
    private ParticleController(
            String name,
            ParticleMesh mesh,
            Geometry geometry,
            int maxParticles,
            float lifeMin,
            float lifeMax) {
        this.name = name;
        this.mesh = mesh;
        this.geometry = geometry;
        this.influencers = new SafeArrayList<ParticleInfluencer>(ParticleInfluencer.class);
        particles = new ParticleData[maxParticles];
        for (int i = 0; i < particles.length; i++) {
            particles[i] = new ParticleData();
        }
        this.lifeMin = lifeMin;
        this.lifeMax = lifeMax;
        this.lifeDiff = lifeMax - lifeMin;
    }

    /**
     * Get the name given to this controller and its geometry
     *
     * @return The name specified (may be null)
     */
    public String getName() {
        return name;
    }

    /**
     * Get the name given to this controller
     * 
     * @param name Set the name to be used for this particle controller and its geometry
     */
    public void setName(String name) {
        this.name = name;
        if (geometry != null) {
            geometry.setName(name);
        }
    }

    /**
     * The source from which particles are spawning.
     *
     * @return The current active source
     */
    public ParticleSource getSource() {
        return source;
    }

    /**
     * Changes the source, new particles will be spawned from the new location
     * but existing particles will not be effected.
     *
     * @param source The new source for particles
     */
    public void setSource(ParticleSource source) {
        this.source = source;
    }

    /**
     * Gets how many particles are currently active.
     * 
     * @return The number of particles which were active last frame.
     */
    public int getActiveCount() {
        return activeCount;
    }
    
    /**
     * The geometry used by the particles. This is created automatically and
     * should be attached to the scene graph. Transforming the geometry will
     * modify all existing particles but will have no adverse effects.
     *
     * @return The geometry used by the particles, this should be attached to
     * the scene graph in a suitable location to make the particles visible.
     */
    public Geometry getGeometry() {
        if (geometry == null) {
            geometry = new Geometry(name, mesh);
            mesh.initializeParticleData(this);
            geometry.addControl(this);
            geometry.setMaterial(mesh.getMaterial());
            geometry.setQueueBucket(RenderQueue.Bucket.Transparent);
        }
        return geometry;
    }

    /**
     * Returns the emission controller currently being used. Note that if a null
     * controller has been specified then the returned value will not be null
     * but will instead be ParticleEmissionController.NULL_EMISSIONS.
     *
     * @return The active ParticleEmissionController
     */
    public ParticleEmissionController getEmissionController() {
        return emissionController;
    }

    /**
     * Set the emission controller to be used, all future emissions will be
     * controlled by this. A value of null will set the emission controller to
     * ParticleEmissionController.NULL_EMISSIONS - which causes no particles to
     * be emitted automatically.
     *
     * @param emissionController The emission controller to use, or null.
     */
    public final void setEmissionController(ParticleEmissionController emissionController) {
        if (emissionController == null) {
            this.emissionController = ParticleEmissionController.NULL_EMISSIONS;
        } else {
            this.emissionController = emissionController;
        }
    }

    /**
     * Returns the maximum number of particles that can be active at any one
     * time.
     *
     * @return The maximum number of particles that can be active at any one
     * time
     */
    public int getMaxParticles() {
        return particles.length;
    }

    /**
     * Sets the minimum and maximum life a particle can have. All new particles
     * will be spawned with a life randomly chosen between these two values
     * (inclusive).
     *
     * @param lifeMin The minimum life in seconds of the particle
     * @param lifeMax The maximum life in seconds of the particle
     */
    public void setLifeMinMax(float lifeMin, float lifeMax) {
        this.lifeMin = lifeMin;
        this.lifeMax = lifeMax;
        this.lifeDiff = lifeMax - lifeMin;
    }

    /**
     * Sets the minimum and maximum life a particle can have. This method sets
     * both to the same value, so all particles will have a constant lifetime.
     *
     * @param lifeMinMax The life in seconds of the particle
     */
    public void setLifeMinMax(float lifeMinMax) {
        this.lifeMin = lifeMinMax;
        this.lifeMax = lifeMinMax;
        this.lifeDiff = 0;
    }

    /**
     * The minimum life of the particle
     *
     * @return The minimum number of seconds for which a particle will live
     */
    public float getLifeMin() {
        return lifeMin;
    }

    /**
     * The maximum life of the particle
     *
     * @return The maximum number of seconds for which a particle will live
     */
    public float getLifeMax() {
        return lifeMax;
    }

    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        camera = vp.getCamera();
    }

    private void updateParticle(int index, float tpf) {
        ParticleData p = particles[index];
        p.life -= tpf;
        if (p.life <= 0) {
            killParticle(index);
        } else {
            p.lifeProgress = 1 - (p.life / p.startlife);

            ParticleInfluencer[] array = influencers.getArray();
            for (int i = 0; i < array.length; i++) {
                array[i].influenceParticle(this, index, p, tpf);
            }
            p.position.addLocal(p.velocity.x * tpf, p.velocity.y * tpf, p.velocity.z * tpf);
        }
    }

    @Override
    public void controlUpdate(float tpf) {
        if (enabled) {
            activeCount = 0;
            
            for (int i = 0; i < particles.length; i++) {
                if (particles[i].active) {
                    activeCount++;
                    updateParticle(i, tpf);
                }
            }

            int emission = emissionController.particlesToSpawn(this, tpf);
            while (emission-- > 0) {
                emitNextParticle();
            }
            
            mesh.updateParticleData(camera, this);
            geometry.updateModelBound();
            
            if (emissionController.shouldAutoDisable() && activeCount == 0) {
                setEnabled(false);
            }
        }
    }

    /**
     * Emits the next available (non-active) particle
     *
     * @return -1 if the particle could not be emitted (most likely because the
     * maximum number were already active), otherwise the index of the emitted
     * particle.
     */
    public int emitNextParticle() {
        int ret = nextIndex;

        if (nextIndex != -1) {
            emitParticle(nextIndex);

            updateNextParticle();
        }
        return ret;
    }

    /**
     * Emits the next available (non-active) particle
     *
     * @return -1 if the particle could not be emitted (most likely because the
     * maximum number were already active), otherwise the index of the emitted
     * particle.
     */
    public int emitNextParticleFrom(Vector3f startLocation, Vector3f startVelocity) {
        int ret = nextIndex;

        if (nextIndex != -1) {
            ParticleData pd = particles[nextIndex];
            pd.position.set(startLocation);
            pd.velocity.set(startVelocity);
            activateParticle(pd, nextIndex);

            updateNextParticle();
        }
        return ret;
    }
    

    /**
     * Emits all non-active particles
     */
    public void emitAllParticles() {
        for (int i = 0; i < particles.length; i++) {
            if (!particles[i].active) {
                emitParticle(i);
            }
        }
        nextIndex = -1;
    }

    private void emitParticle(int index) {
        ParticleData pd = particles[index];
        source.sourceParticle(this, index, pd);
        activateParticle(pd, index);
    }

    /**
     * Deactivates and resets the specified particle. Note that killing a particle
     * by index is more efficient than killing it by ParticleData so the other
     * form of this method is recommended.
     *
     * @param p The particle to reset
     */
    public void killParticle(ParticleData p) {
        for (int i = 0; i < particles.length; i++) {
            if (particles[i] == p) {
                killParticle(i);
                break;
            }
        }
    }

    /**
     * Deactivates and resets the specified particle
     *
     * @param index The index of the particle to reset
     */
    public void killParticle(int index) {
        particles[index].active = false;
        setNextIndex(index);
        emissionController.notifyParticleDeath(this, index);
    }

    /**
     * Kill all particles.
     */
    public void killAllParticles() {
        for (int i=0;i<particles.length;i++) {
            killParticle(i);
        }
    }
    
    private void setNextIndex(int index) {
        if (index < nextIndex || nextIndex == -1) {
            nextIndex = index;
        }
    }

    /**
     * Gets the array of particle data in its current state. Please note that
     * this is the live store, alterations to particles will be reflected in the
     * mesh and should only be done on the render thread. It is not recommended
     * that this be used to make changes though, instead implement the
     * ParticleInfluencer interface and add a new influencer with the behaviour
     * you require.
     *
     * @return An array of particle data
     */
    public ParticleData[] getParticles() {
        return particles;
    }

    /**
     * Gets the mesh
     *
     * @return The mesh object being used to display the particles
     */
    public ParticleMesh getMesh() {
        return mesh;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(name, "name", null);
        oc.write(mesh, "mesh", null);
        oc.write(source, "source", null);
        oc.write(emissionController, "emissionController", null);
        oc.writeSavableArrayList(new ArrayList(influencers), "influencers", null);
        oc.write(lifeMin, "lifeMin", 1);
        oc.write(lifeMax, "lifeMin", 2);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        name = ic.readString("name", null);
        mesh = (ParticleMesh) ic.readSavable("mesh", null);
        source = (ParticleSource) ic.readSavable("source", null);
        emissionController = (ParticleEmissionController) ic.readSavable("emissionController", null);
        influencers = new SafeArrayList<ParticleInfluencer>(ParticleInfluencer.class, ic.readSavableArrayList("influencers", null));
        lifeMin = ic.readFloat("lifeMin", 1f);
        lifeMax = ic.readFloat("lifeMax", 2f);
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        Geometry geo = (Geometry)spatial;
        
        // This constructor will handle cloning of passed params
        ParticleController clone = new ParticleController(
                name,
                (ParticleMesh)(geo.getMesh()),
                geo,
                particles.length,
                lifeMin,
                lifeMax);
        
        clone.source = source.cloneForController(this);
        clone.setEmissionController(emissionController.cloneForController(this));
        for (ParticleInfluencer pi: influencers) {
            clone.influencers.add(pi.cloneForController(this));
        }

        return clone;
    }

    private void updateNextParticle() {
        int searchIndex = nextIndex;
        do {
            searchIndex++;
            if (searchIndex >= particles.length) {
                searchIndex = 0;
            }
            if (searchIndex == nextIndex) {
                searchIndex = -1;
                break;
            }
        } while (particles[searchIndex].active);

        nextIndex = searchIndex;
    }

    private void activateParticle(ParticleData pd, int index) {
        pd.activate(lifeMin + lifeDiff * FastMath.nextRandomFloat());
        ParticleInfluencer[] array = influencers.getArray();
        for (int i = 0; i < array.length; i++) {
            array[i].influenceParticleCreation(this, index, pd);
        }
        activeCount ++;
        
        if (!enabled && emissionController.shouldAutoDisable()) {
            setEnabled(true);
        }
    }
}
