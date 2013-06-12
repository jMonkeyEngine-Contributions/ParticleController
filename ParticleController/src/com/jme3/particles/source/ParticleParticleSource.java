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
package com.jme3.particles.source;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleSource;
import java.io.IOException;

/**
 *
 * @author Tim
 */
public class ParticleParticleSource implements ParticleSource {

    ParticleController source;
    private static final Quaternion working = new Quaternion();

    public ParticleParticleSource(ParticleController source) {
        this.source = source;
    }
    
    @Override
    public void sourceParticle(ParticleController pCtrl, int index, ParticleData particle) {
        ParticleData[] particles = source.getParticles();

        int count = 0;
        for (int i=0;i<particles.length;i++) {
            if (particles[i].active)
                count++;
        }
        count = FastMath.nextRandomInt(0, count-1);
        for (int i=0;i<count && i<particles.length;i++) {
            if (!particles[i].active)
                count++;
        }
        
        particle.position.set(particles[count].position);
        particle.velocity.set(particles[count].velocity);
        particle.size = particles[count].size;
        particle.rotationalVelocity.set(particles[count].rotationalVelocity);
        particle.rotation.set(particles[count].rotation);
        
        // First convert everything to world space
        Transform t = source.getGeometry().getWorldTransform();
        t.transformVector(particle.position, particle.position);
        t.getRotation().mult(particle.velocity, particle.velocity);
        particle.rotation.multLocal(t.getRotation());
        
        // Now convert everything to mesh space
        t = pCtrl.getGeometry().getWorldTransform();
        t.transformInverseVector(particle.position, particle.position);
        working.set(t.getRotation());
        working.inverseLocal();
        working.mult(particle.velocity, particle.velocity);
        particle.rotation.multLocal(working);
    }

    @Override
    public ParticleSource cloneForController(ParticleController controller) {
        return new ParticleParticleSource(source);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
    }

    @Override
    public void read(JmeImporter im) throws IOException {
    }
    
}
