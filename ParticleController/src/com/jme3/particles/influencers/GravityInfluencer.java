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
import com.jme3.math.Vector3f;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleInfluencer;
import java.io.IOException;

/**
 * The GravityInfluencer applies continuous acceleration to all of the particles
 * in the given direction. Note that gravity is applied in the particle mesh 
 * space and not in world space. If the particle mesh is rotated or scaled then 
 * so will gravity effects be.
 */
public class GravityInfluencer implements ParticleInfluencer {

    private Vector3f gravity = new Vector3f(0, -1f, 0);

    /**
     * Construct a new GravityInfluencer with the default gravity settings (0, -1, 0)
     */
    public GravityInfluencer() {
    }

    /**
     * Construct a new GravityInfluencer with the supplied gravity.
     * 
     * @param gravity The gravity vector
     */
    public GravityInfluencer(Vector3f gravity) {
        this.gravity.set(gravity);
    }
    
    /**
     * Sets gravity to the provided Vector3f
     *
     * @param gravity Vector3f representing gravity
     */
    public void setGravity(Vector3f gravity) {
        this.gravity.set(gravity);
    }

    /**
     * Sets gravity per axis to the specified values.
     *
     * @param x Gravity along the x axis
     * @param y Gravity along the y axis
     * @param z Gravity along the z axis
     */
    public void setGravity(float x, float y, float z) {
        this.gravity.set(x, y, z);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(gravity, "gravity", new Vector3f(0, 1, 0));
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        gravity = (Vector3f) ic.readSavable("gravity", new Vector3f(0, 1, 0));
    }

    @Override
    public ParticleInfluencer clone() {
        try {
            GravityInfluencer clone = (GravityInfluencer) super.clone();
            clone.setGravity(gravity);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public void influenceParticleCreation(ParticleController ctrl, int index, ParticleData data) {
    }

    @Override
    public void influenceParticle(ParticleController ctrl, int index, ParticleData data, float tpf) {
        data.velocity.x += gravity.x * tpf;
        data.velocity.y += gravity.y * tpf;
        data.velocity.z += gravity.z * tpf;
    }

    @Override
    public ParticleInfluencer cloneForController(ParticleController controller) {
        return new GravityInfluencer();
    }
}
