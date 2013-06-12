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
package com.jme3.particles.source;

import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleSource;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import java.io.IOException;
import java.util.Queue;

/**
 * A PointSource is a single point in space from which all particles will be
 * emitted. The particles will be emitted with a random velocity between the two
 * specified values.
 * 
 * PointSource does not need to be added to the scene graph but it extends Spatial 
 * so can be if needed. The spawning of particles will follow the movement of the
 * PointSource so for example attaching it to a node will cause the particles to
 * always appear from the location of that node.
 */
public class PointSource extends Spatial implements ParticleSource {

    private Vector3f minInitialVelocity;
    private Vector3f initialVelocityRange;

    /**
     * Create a new PointSource which will generate particles with a velocity between
     * the supplied minimum and maximum.
     * 
     * @param minInitialVelocity The minimum initial velocity of particles
     * @param maxInitialVelocity The maximum initial velocity of particles
     */
    public PointSource(Vector3f minInitialVelocity, Vector3f maxInitialVelocity) {
        this.minInitialVelocity = minInitialVelocity;
        this.initialVelocityRange = maxInitialVelocity.subtract(minInitialVelocity);
    }

    /**
     * Create a new PointSource which will generate particles with a velocity between
     * the supplied minimum and maximum. The spatial will be shown with the supplied name
     * in the scene graph.
     * 
     * @param minInitialVelocity The minimum initial velocity of particles
     * @param maxInitialVelocity The maximum initial velocity of particles
     * @param name The name of this PointSource
     */
    public PointSource(Vector3f minInitialVelocity, Vector3f maxInitialVelocity, String name) {
        super(name);
        this.minInitialVelocity = minInitialVelocity;
        this.initialVelocityRange = maxInitialVelocity.subtract(minInitialVelocity);
    }
    
    @Override
    public void updateModelBound() {
    }

    @Override
    public void setModelBound(BoundingVolume modelBound) {
    }

    @Override
    public int getVertexCount() {
        return 0;
    }

    @Override
    public int getTriangleCount() {
        return 0;
    }

    @Override
    public Spatial deepClone() {
        return this.clone();
    }

    @Override
    public void depthFirstTraversal(SceneGraphVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    protected void breadthFirstTraversal(SceneGraphVisitor visitor, Queue<Spatial> queue) {
    }

    @Override
    public int collideWith(Collidable other, CollisionResults results) throws UnsupportedCollisionException {
        return 0;
    }

    @Override
    public void sourceParticle(ParticleController pCtrl, int index, ParticleData particle) {
        particle.initialise(new Vector3f(
                minInitialVelocity.x+FastMath.nextRandomFloat()*initialVelocityRange.x, 
                minInitialVelocity.y+FastMath.nextRandomFloat()*initialVelocityRange.y, 
                minInitialVelocity.z+FastMath.nextRandomFloat()*initialVelocityRange.z), 
                getWorldTranslation());
    }

    @Override
    public ParticleSource cloneForController(ParticleController controller) {
        return new PointSource(minInitialVelocity, initialVelocityRange.add(minInitialVelocity), name);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(minInitialVelocity, "minInitialVelocity", null);
        capsule.write(initialVelocityRange, "initialVelocityRange", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule capsule = im.getCapsule(this);
        minInitialVelocity = (Vector3f) capsule.readSavable( "minInitialVelocity", null);
        initialVelocityRange = (Vector3f) capsule.readSavable( "initialVelocityRange", null);
    }

    
}
