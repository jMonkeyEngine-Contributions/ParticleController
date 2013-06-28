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

import com.jme3.scene.Node;

    /**
     * Constructs a new ParticleController with the given name (optional) and
     * the behaviour as specified by the passed parameters.
     * 
     * This version of a ParticleController will automatically add and remove itself
     * from the specified Node of the scene graph when it is enabled and disabled,
     * this can be useful for controllers that emit particles at specific times and
     * then go idle as they will not be processed at all while idle.
     *
     * One the ParticleController has been constructed you can query the
     * geometry using getGeometry() and attach the geometry to the scene graph
     * in any suitable location. Transforming that geometry will move all of the
     * particles but all ParticleSource and ParticleInfluencers will continue to
     * operate as would be expected.
     *
     * Removing the geometry from the scene graph will pause the particles and
     * the ParticleController and it will consume no resources (other than
     * memory!). To activate it again simply re-attach it.
     *
     * @param targetNode The node to attach and detach the controller too and from
     * @param name The name to use for the geometry in the scene graph
     * @param mesh The mesh to use (Usually either PointMesh or QuadMesh)
     * @param maxParticles The maximum number of particles to allow active at
     * any one time
     * @param lifeMin The minimum amount of time (in seconds) for which each particle lives
     * @param lifeMax The maximum amount of time (in seconds) for which each particle lives
     * @param source The source from which the particles are spawned
     * @param emissionController The frequency and timing with which particles are
     * spawned. If null then no particles are automatically spawned and they
     * must be triggered manually using emitNextParticle() or emitAllParticles()
     * @param influencers Zero or more ParticleInfluencers, each of which
     * changes the behaviour of the particles.
     */
public class SelfRemovingParticleController extends ParticleController {
    
    final Node targetNode;

    public SelfRemovingParticleController(Node targetNode, String name, ParticleMesh mesh, int maxParticles, float lifeMin, float lifeMax, ParticleSource source, ParticleEmissionController emissionController, ParticleInfluencer... influencers) {
        super(name, mesh, maxParticles, lifeMin, lifeMax, source, emissionController, influencers);
        this.targetNode = targetNode;
        super.setEnabled(false);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            targetNode.attachChild(getGeometry());
        } else {
            targetNode.detachChild(getGeometry());
        }
        super.setEnabled(enabled);
    }
    
}
