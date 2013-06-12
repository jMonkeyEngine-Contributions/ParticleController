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
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleInfluencer;
import java.io.IOException;

/**
 * The rotation influencer should be added to any ParticleController where the
 * particles are rotated. It both sets up the initial rotational velocity of
 * the particle and ensures that they rotate correctly each frame.
 */
public class RotationInfluencer implements ParticleInfluencer {

    private Vector3f minRotationalVelocity;
    private Vector3f rotationalVelocityRange = new Vector3f();
    private boolean randomlyFlipVelocity;
    
    private static Quaternion working = new Quaternion();

    /**
     * Construct a new RotationInfluencer with the given parameters.
     * 
     * @param minRotationalVelocity The minimum rotational velocity on each axis for new particles
     * @param maxRotationalVelocity The maximum rotational velocity on each axis for new particles
     * @param randomlyFlipVelocity If set then each axis will be randomly flipped (for example if you want
     * particles that always rotate at a certain speed but want the direction to be random you could
     * set a fixed velocity and then turn on random flip).
     */
    public RotationInfluencer(Vector3f minRotationalVelocity, Vector3f maxRotationalVelocity, boolean randomlyFlipVelocity) {
        this.minRotationalVelocity = minRotationalVelocity;
        this.rotationalVelocityRange.set(
            maxRotationalVelocity.x-minRotationalVelocity.x, 
            maxRotationalVelocity.y-minRotationalVelocity.y, 
            maxRotationalVelocity.z-minRotationalVelocity.z);
        this.randomlyFlipVelocity = randomlyFlipVelocity;
    }
    
    /**
     * Get the minimum rotational velocity
     * 
     * @return The minimum rotational velocity
     */
    public Vector3f getMinRotationalVelocity() {
        return minRotationalVelocity;
    }

    /**
     * Set the minimum rotational velocity
     * 
     * @param minRotationalVelocity The new minimum rotational velocity
     */
    public void setMinRotationalVelocity(Vector3f minRotationalVelocity) {
        this.minRotationalVelocity = minRotationalVelocity;
    }

    /**
     * Set the maximum rotational velocity
     * 
     * @param maxRotationalVelocity The new maximum rotational velocity
     */
    public void setMaxRotationalVelocity(Vector3f maxRotationalVelocity) {
        this.rotationalVelocityRange.set(
            maxRotationalVelocity.x-minRotationalVelocity.x, 
            maxRotationalVelocity.y-minRotationalVelocity.y, 
            maxRotationalVelocity.z-minRotationalVelocity.z);
    }

    /**
     * @return Whether velocity is randomly flipped
     */
    public boolean isRandomlyFlipVelocity() {
        return randomlyFlipVelocity;
    }
    
    /**
     * Set whether velocity is randomly flipped
     * 
     * @param randomlyFlipVelocity Set random flip to true or false
     */
    public void setRandomlyFlipVelocity(boolean randomlyFlipVelocity) {
        this.randomlyFlipVelocity = randomlyFlipVelocity;
    }
    
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(minRotationalVelocity, "minRotationalVelocity", Vector3f.ZERO);
        oc.write(rotationalVelocityRange, "rotationalVelocityRange", Vector3f.ZERO);
        oc.write(randomlyFlipVelocity, "randomlyFlipVelocity", Boolean.TRUE);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        minRotationalVelocity = (Vector3f) ic.readSavable("minRotationalVelocity", Vector3f.ZERO.clone());
        rotationalVelocityRange = (Vector3f) ic.readSavable("rotationalVelocityRange", Vector3f.ZERO.clone());
        randomlyFlipVelocity = ic.readBoolean("randomlyFlipVelocity", Boolean.TRUE);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        RotationInfluencer clone = (RotationInfluencer) super.clone();
        clone.minRotationalVelocity = new Vector3f(minRotationalVelocity);
        clone.rotationalVelocityRange = new Vector3f(rotationalVelocityRange);
        return clone;
    }

    @Override
    public void influenceParticleCreation(ParticleController ctrl, int index, ParticleData data) {
        data.rotationalVelocity.set(
                (minRotationalVelocity.x+FastMath.nextRandomFloat()*rotationalVelocityRange.x),
                (minRotationalVelocity.y+FastMath.nextRandomFloat()*rotationalVelocityRange.y),
                (minRotationalVelocity.z+FastMath.nextRandomFloat()*rotationalVelocityRange.z));
        if (randomlyFlipVelocity) {
            if (FastMath.nextRandomInt() % 2 == 0)
                data.rotationalVelocity.x = -data.rotationalVelocity.x;
            if (FastMath.nextRandomInt() % 2 == 0)
                data.rotationalVelocity.y = -data.rotationalVelocity.y;
            if (FastMath.nextRandomInt() % 2 == 0)
                data.rotationalVelocity.z = -data.rotationalVelocity.z;
        }
    }

    @Override
    public void influenceParticle(ParticleController ctrl, int index, ParticleData data, float tpf) {
        working.fromAngles(data.rotationalVelocity.x * tpf, data.rotationalVelocity.y * tpf, data.rotationalVelocity.z * tpf);
        data.rotation.multLocal(working);
    }

    @Override
    public ParticleInfluencer cloneForController(ParticleController controller) {
        return new RotationInfluencer(minRotationalVelocity, rotationalVelocityRange.add(minRotationalVelocity), randomlyFlipVelocity);
    }
}
