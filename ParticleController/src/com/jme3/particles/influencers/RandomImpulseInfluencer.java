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

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleInfluencer;
import java.io.IOException;

/**
 * This influencer applies a random impulse to the particles either as it was
 * first created, every frame, or both.
 */
public class RandomImpulseInfluencer implements ParticleInfluencer {

    
    public enum ImpulseApplicationTime {
        INITIALIZE,
        UPDATE,
        ALWAYS;
    }
    
    private ImpulseApplicationTime applicationTime;
    
    private Vector3f minImpulse;
    private Vector3f impulseRange;

    /**
     * Construct a new RandomImpulseInfluencer with the given parameters 
     * 
     * @param applicationTime When the impulse should be applied
     * @param minImpulse The minimum value for the impulse
     * @param maxImpulse The maximum value for the impulse
     */
    public RandomImpulseInfluencer(ImpulseApplicationTime applicationTime, Vector3f minImpulse, Vector3f maxImpulse) {
        this.applicationTime = applicationTime;
        this.minImpulse = minImpulse;
        this.impulseRange = maxImpulse.subtract(minImpulse);
    }

    @Override
    public void influenceParticleCreation(ParticleController ctrl, int index, ParticleData data) {
        if (applicationTime != ImpulseApplicationTime.UPDATE) {
            data.velocity.addLocal(
                    minImpulse.x + FastMath.nextRandomFloat()*impulseRange.x,
                    minImpulse.y + FastMath.nextRandomFloat()*impulseRange.y,
                    minImpulse.z + FastMath.nextRandomFloat()*impulseRange.z);
        }
    }

    @Override
    public void influenceParticle(ParticleController ctrl, int index, ParticleData data, float tpf) {
        if (applicationTime != ImpulseApplicationTime.INITIALIZE) {
            data.velocity.addLocal(
                    (minImpulse.x + FastMath.nextRandomFloat()*impulseRange.x)*tpf,
                    (minImpulse.y + FastMath.nextRandomFloat()*impulseRange.y)*tpf,
                    (minImpulse.z + FastMath.nextRandomFloat()*impulseRange.z)*tpf);
        }
    }

    @Override
    public ParticleInfluencer cloneForController(ParticleController controller) {
        return new RandomImpulseInfluencer(applicationTime, minImpulse, minImpulse.add(impulseRange));
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(minImpulse, "minImpulse", null);
        capsule.write(impulseRange, "impulseRange", null);
        capsule.write(applicationTime, "applicationTime", ImpulseApplicationTime.ALWAYS);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        minImpulse = (Vector3f) capsule.readSavable("minImpulse", null);
        impulseRange = (Vector3f) capsule.readSavable("impulseRange", null);
        applicationTime = capsule.readEnum("applicationTime", ImpulseApplicationTime.class, ImpulseApplicationTime.ALWAYS);
    }    
}
