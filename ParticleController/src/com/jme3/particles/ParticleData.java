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

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;


/**
 * This class contains all of the data about an active particle which can be modified
 * by the ParticleInfluencers and displayed by the mesh.
 */
public class ParticleData {
    
    /**
     * ParticleData velocity.
     */
    public final Vector3f velocity = new Vector3f();
    /**
     * Current particle position
     */
    public final Vector3f position = new Vector3f();
    /**
     * ParticleData color
     */
    public final ColorRGBA color = new ColorRGBA(1, 1, 1, 1);
    /**
     * ParticleData size or radius in world units.
     */
    public float size;
    /**
     * ParticleData remaining life, in seconds.
     */
    public float life;
    /**
     * The initial particle life in seconds
     */
    public float startlife;
    /**
     * ParticleData rotation.
     */
    public Quaternion rotation = new Quaternion();
    /**
     * ParticleData rotational velocity per axis (in radians per second).
     */
    public Vector3f rotationalVelocity = new Vector3f();
    /**
     * ParticleData image index.
     */
    public int spriteCol, spriteRow;
    /**
     * The state of the particle, inactive particles will not be displayed.
     */
    public boolean active = false;
    /**
     * How far through the life of this particle it is (linear progression from 0-1 from spawn to end of life)
     */
    public float lifeProgress = 0;

    /**
     * Called by the ParticleSource to initialize the position and velocity of the particle.
     * 
     * @param velocity The starting velocity of the particle
     * @param position The starting position of the particle
     */
    public void initialise(Vector3f velocity, Vector3f position) {
        this.velocity.set(velocity);
        this.position.set(position);
    }

    /**
     * Called by the ParticleController to activate the particle with a lifespan as specified.
     * 
     * @param life The number of seconds the particle will be active for
     */
    public void activate(float life) {
        this.rotationalVelocity.set(0, 0, 0);
        this.rotation.set(Quaternion.IDENTITY);
        
        this.life = life;
        this.startlife = life;
        
        active = true;
        size = 1;
        
        spriteCol = 0;
        spriteRow = 0;
    }
}
