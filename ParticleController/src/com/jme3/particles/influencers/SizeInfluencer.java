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
public class SizeInfluencer implements ParticleInfluencer {

    private float startSize = 1;
    private float endSize = 0;

    /**
     * Sets the initial size of the particle when emitted
     *
     * @param size The start size of the particle
     */
    public void setStartSize(float size) {
        this.startSize = size;
    }

    /**
     * Returns the size the particle uses when emitted
     *
     * @return The initial size of the particle when emitted
     */
    public float getStartSize() {
        return this.startSize;
    }

    /**
     * Sets the size the particle will reach at at the end of it's life cycle
     *
     * @param size The size the particle will be when it's life cycle has
     * completed
     */
    public void setEndSize(float size) {
        this.endSize = size;
    }

    /**
     * Returns the size the particle will be when it's life cycle has completed
     *
     * @return The size the particle will be when it's life cycle has completed
     */
    public float getEndSize() {
        return this.endSize;
    }

    /**
     * Construct a new SizeInfluencer with the given parameters.
     * 
     * @param startSize The initial size for particles
     * @param endSize The final size for particles
     */
    public SizeInfluencer(float startSize, float endSize) {
        this.startSize = startSize;
        this.endSize = endSize;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(startSize, "startSize", 1f);
        oc.write(endSize, "endSize", 0f);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        startSize = ic.readFloat("startSize", 1f);
        endSize = ic.readFloat("endSize", 0f);
    }

    @Override
    public void influenceParticleCreation(ParticleController ctrl, int index, com.jme3.particles.ParticleData data) {
        data.size = startSize;
    }

    @Override
    public void influenceParticle(ParticleController ctrl, int index, ParticleData data, float tpf) {
        data.size = FastMath.interpolateLinear(data.lifeProgress, startSize, endSize);
    }

    @Override
    public ParticleInfluencer cloneForController(ParticleController controller) {
        return new SizeInfluencer(startSize, endSize);
    }
}
