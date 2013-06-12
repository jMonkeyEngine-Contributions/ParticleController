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
package com.jme3.particles;

import com.jme3.export.Savable;

/**
 * ParticleInfluencers are used to modify the behaviour of particles, both when
 * they are initially created and their movements each frame. If a ParticleInfluencer
 * is modifying the movement of a particle then it generally does so by modifying
 * the velocity rather than directly modifying position.
 */
public interface ParticleInfluencer extends Savable, Cloneable {

    /**
     * This method is called on each influencer for each particle when a new particle
     * is emitted.
     * 
     * @param ctrl The ParticleController emitting the particle
     * @param index The particle index
     * @param data The ParticleData for this particle
     */
    void influenceParticleCreation(ParticleController ctrl, int index, ParticleData data);
    
    /**
     * 
     * This method is called on each influencer for each particle each frame.
     * 
     * @param ctrl The ParticleController controlling the particle
     * @param index The particle index
     * @param data The ParticleData for this particle
     * @param tpf The time-per-frame value for this frame
     */
    void influenceParticle(ParticleController ctrl, int index, ParticleData data, float tpf);

    /**
     * Called when the particle emitter is cloned to ensure all sources are
     * also cloned if need be.
     * 
     * @param controller The new controller the clone will be working for
     * @return The clone
     */
    public ParticleInfluencer cloneForController(ParticleController controller);
    
}
