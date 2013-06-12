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
package com.jme3.particles.emissioncontrollers;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleEmissionController;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a basic implementation of a ParticleEmissionController that emits
 * particles on a regular basis.
 */
public class RegularEmission implements ParticleEmissionController {
    float emissionTime;
    float accumulatedTime;

    /**
     * Construct a new RegularEmission.
     * 
     * @param emissionsPerSecond How many particles should be emitted each second
     */
    public RegularEmission(int emissionsPerSecond) {
        setEmissionsPerSecond(emissionsPerSecond);
        this.accumulatedTime = 0;
    }

    /**
     * Modify the emission rate.
     * 
     * @param emissionsPerSecond How many particles should be emitted each second
     */
    public final void setEmissionsPerSecond(int emissionsPerSecond) {
        this.emissionTime = 1f/emissionsPerSecond;
    }

    @Override
    public int particlesToSpawn(ParticleController ctrlr, float tpf) {
        accumulatedTime += tpf;
        int result = (int)(accumulatedTime / emissionTime);
        accumulatedTime -= emissionTime*result;
        return result;
    }

    @Override
    public ParticleEmissionController cloneForController(ParticleController controller) {
        return new RegularEmission((int)(1f/emissionTime));
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        ex.getCapsule(this).write(emissionTime, "emissionTime", 0);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        im.getCapsule(this).readFloat("emissionTime", 0);
    }

    @Override
    public void notifyParticleDeath(ParticleController ctrlr, int particleIndex) {
    }
    
}
