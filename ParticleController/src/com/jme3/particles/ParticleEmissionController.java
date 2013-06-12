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

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.Savable;
import java.io.IOException;


/**
 * Implement this interface in order to control the rate at which particles are
 * spawned. For most cases a simple emissioncontrollers.RegularEmission will
 * suffice. There is also a NULL_EMISSIONS controller provided which causes
 * no automatic spawning, particles must be spawned manually.
 */
public interface ParticleEmissionController extends Savable {

    /**
     * An emission controller that allows manual control of when particles are
     * emitted.
     */
    public static final ParticleEmissionController NULL_EMISSIONS = new ParticleEmissionController() {

        @Override
        public void write(JmeExporter ex) throws IOException {
        }

        @Override
        public void read(JmeImporter im) throws IOException {
        }

        @Override
        public ParticleEmissionController cloneForController(ParticleController controller) {
            return this;
        }

        @Override
        public int particlesToSpawn(ParticleController ctrlr, float tpf) {
            return 0;
        }

        @Override
        public void notifyParticleDeath(ParticleController ctrlr, int particleIndex) {
        }
    };
    
    /**
     * The ParticleController calls this every frame to determine how many particles
     * should be emitted this frame.
     * 
     * @param ctrlr The particle controller emitting the particles
     * @param tpf The time-per-frame, which can be used to modify the number emitted.
     * @return The number of particles to spawn this frame.
     */
    public int particlesToSpawn(ParticleController ctrlr, float tpf);

    /**
     * Called by the controller when particles die in case this emitter wishes to
     * do anything with the information
     * 
     * @param ctrlr The particle controller emitting the particles
     * @param particleIndex The index of the particle which has died
     */
    public void notifyParticleDeath(ParticleController ctrlr, int particleIndex);
    
    /**
     * Called when the particle emitter is cloned to ensure all emission controllers are
     * also cloned if need be.
     * 
     * @param controller The new controller the clone will be working for
     * @return The clone
     */
    public ParticleEmissionController cloneForController(ParticleController controller);
    
}
