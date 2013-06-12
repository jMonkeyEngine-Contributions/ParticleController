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
 * This influencer sets the speed of the particle (without changing its direction)
 * to the given values, interpolating between them over the particles life span.
 */
public class SpeedInfluencer implements ParticleInfluencer {
    
    private float startSpeed;
    private float endSpeed;

    /**
     * Construct a new SpeedInfluencer with the specified start and end speeds.
     * @param startSpeed
     * @param endSpeed 
     */
    public SpeedInfluencer(float startSpeed, float endSpeed) {
        this.startSpeed = startSpeed;
        this.endSpeed = endSpeed;
    }
    
    @Override
    public void influenceParticleCreation(ParticleController ctrl, int index, ParticleData data) {
        data.velocity.normalizeLocal().multLocal(startSpeed);
    }

    @Override
    public void influenceParticle(ParticleController ctrl, int index, ParticleData data, float tpf) {
        data.velocity.normalizeLocal().multLocal(FastMath.interpolateLinear(data.lifeProgress, startSpeed, endSpeed));
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(startSpeed, "startSpeed", 0);
        capsule.write(endSpeed, "endSpeed", 0);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        startSpeed = capsule.readFloat("startSpeed", 0);
        endSpeed = capsule.readFloat("endSpeed", 0);
    }

    @Override
    public ParticleInfluencer cloneForController(ParticleController controller) {
        return new SpeedInfluencer(startSpeed, endSpeed);
    }
    
}
