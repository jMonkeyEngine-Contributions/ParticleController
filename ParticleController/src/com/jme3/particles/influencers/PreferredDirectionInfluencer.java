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
import java.io.IOException;

public class PreferredDirectionInfluencer implements ParticleInfluencer {

    private Vector3f direction = new Vector3f();
    private float timeBeforeEnd = 1;

    /**
     * Influences particles towards the given direction with the given strength,
     * this will not change the velocity of the particle.
     * 
     * @param direction The direction to influence towards, this should be a unit
     * vector.
     * @param timeBeforeEnd How long before the end of the particles life the convergence should have finished.
     */
    public PreferredDirectionInfluencer(Vector3f direction, float timeBeforeEnd) {
        this.direction.set(direction);
        this.timeBeforeEnd = timeBeforeEnd;
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
        float totalTime = data.startlife - timeBeforeEnd;
        float time = timeSoFar/totalTime;
        float speed = data.velocity.length();
        
        if (time >= 1) {
            data.velocity.set(direction);
            data.velocity.multLocal(speed);
        } else {
            data.velocity.divideLocal(speed);
            data.velocity.interpolate(direction, time);
            float speedFactor = speed/data.velocity.length();
            data.velocity.multLocal(speedFactor);
        }
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        ex.getCapsule(this).write(direction, "direction", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        direction = (Vector3f) im.getCapsule(this).readSavable("direction", null);
        timeBeforeEnd = im.getCapsule(this).readFloat("timeBeforeEnd", 0);
    }

    @Override
    public ParticleInfluencer cloneForController(ParticleController controller) {
        return new PreferredDirectionInfluencer(direction.clone(), timeBeforeEnd);
    }
    
    
}
