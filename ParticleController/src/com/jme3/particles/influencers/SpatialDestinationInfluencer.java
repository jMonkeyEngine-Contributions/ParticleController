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
package com.jme3.particles.influencers;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.Vector3f;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleInfluencer;
import com.jme3.scene.Spatial;
import java.io.IOException;

/**
 * The SpatialDestinationInfluencer causes particles to chase a given spatial. They
 * will try and reach the origin of that spatial by the end of the particles life,
 * although if the target is moving they may not quite catch up. There is no requirement
 * for the source and destination to be in the same part of the scene graph and any
 * required coordinate transformations are automatically performed.
 */
public class SpatialDestinationInfluencer implements ParticleInfluencer {

    private Spatial destination;
    private static final Vector3f working = new Vector3f();

    /**
     * Influences particles towards the given destination, this will modify
     * the direction and speed of the particle so that it reaches the destination
     * at the end of the particle's life.
     * 
     * @param destination The Spatial to influence towards
     */
    public SpatialDestinationInfluencer(Spatial destination) {
        this.destination = destination;
    }
    
    @Override
    public void influenceParticleCreation(ParticleController ctrl, int index, ParticleData data) {
    }

    // Note that this is a very simple linear influence that will not produce particularly smooth
    // rotation of velocity but will converge in all cases except when the velocity is perfectly
    // opposite the intended velocity.
    @Override
    public void influenceParticle(ParticleController ctrl, int index, ParticleData data, float tpf) {
        
        float timeSoFar = data.startlife-data.life;
        float time = timeSoFar/data.startlife;
        
        ctrl.getGeometry().getWorldTransform().transformInverseVector(destination.getWorldTranslation(), working);
        working.subtractLocal(data.position);
        
        if (time >= 1) {
            data.velocity.set(working);
        } else {
            data.velocity.interpolate(working, time);
        }
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        ex.getCapsule(this).write(destination, "destination", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        im.getCapsule(this).readSavable("destination", null);
    }

    @Override
    public ParticleInfluencer cloneForController(ParticleController controller) {
        return new SpatialDestinationInfluencer(destination.clone());
    }
    
}
