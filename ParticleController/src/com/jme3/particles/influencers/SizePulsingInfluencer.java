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
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleInfluencer;
import java.io.IOException;

/**
 * The SizeInfluencer modifies the size of a particle over the course of its
 * lifetime.
 */
public class SizePulsingInfluencer implements ParticleInfluencer {

    private float minSize = 0;
    private float maxSize = 1;
    private float numCycles = 1;

    private float progressMult;


    /**
     * Construct a new SizeInfluencer with the given parameters.
     * 
     * @param minSize The smallest size for the particles
     * @param maxSize The largest size for the particles
     * @param numCycles The number of full cycles from min to max then back to min to
     * do over the lifespan of the particle
     */
    public SizePulsingInfluencer(float minSize, float maxSize, float numCycles) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.numCycles = numCycles;
        this.progressMult = FastMath.PI*numCycles;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(minSize, "minSize", 0f);
        oc.write(maxSize, "maxSize", 1f);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        minSize = ic.readFloat("minSize", 0f);
        maxSize = ic.readFloat("maxSize", 1f);
        progressMult = FastMath.PI*numCycles/2;
    }

    @Override
    public void influenceParticleCreation(ParticleController ctrl, int index, com.jme3.particles.ParticleData data) {
        data.size = minSize;
    }

    @Override
    public void influenceParticle(ParticleController ctrl, int index, ParticleData data, float tpf) {
        data.size = minSize + (FastMath.sin(data.lifeProgress*progressMult)+1)*maxSize/2;
    }

    @Override
    public ParticleInfluencer cloneForController(ParticleController controller) {
        return new SizePulsingInfluencer(minSize, maxSize, numCycles);
    }
}
