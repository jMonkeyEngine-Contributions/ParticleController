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

import com.jme3.material.Material;
import com.jme3.renderer.Camera;
import com.jme3.scene.Mesh;

/**
 * The ParticleMesh is used to build various mesh solutions to display the particle
 * data. It provides the common basis on which all the solutions are built.
 */
public abstract class ParticleMesh extends Mesh {
    
    protected Material material;

    protected int spriteCols;
    protected int spriteRows;
    protected boolean uniqueTexCoords;

    /**
     * Construct a new particle mesh which will use the provided material to display
     * the particles. The particle texture is divided into spriteCols*spriteRows
     * sprites.
     */
    public ParticleMesh(Material material, int spriteCols, int spriteRows) {
        this.material = material;
        setSpriteColumsAndRows(spriteCols, spriteRows);
    }

    /**
     * Set the number of columns and rows in the particle texture.
     * 
     * @param columns The number of columns
     * @param rows The number of rows
     */
    public final void setSpriteColumsAndRows(int columns, int rows) {
        this.spriteCols = columns;
        this.spriteRows = rows;
        uniqueTexCoords = columns != 1 || rows != 1;
    }

    /**
     * 
     * @return The number of columns into which the texture is divided
     */
    public int getSpriteCols() { return this.spriteCols; }
    
    /**
     * 
     * @return The number of rows into which the texture is divided
     */
    public int getSpriteRows() { return this.spriteRows; }

    public Material getMaterial() {
        return material;
    }

    /**
     * Called by the ParticleController when it first starts up in order to
     * allow the ParticleMesh to be set up with the correct capacity, etc.
     * 
     * @param controller The ParticleController for which the data should be initialised
     */
    public abstract void initializeParticleData(ParticleController controller);

    /**
     * Called by the ParticleController each frame to allow the display of the
     * particles to be updated to reflect their new status. The Camera may be null
     * if the particles are not currently being displayed on screen and all ParticleMesh
     * implementations must cope with being passed a null Camera.
     * 
     * @param cam The camera on which the particles are being shown or null if they are not
     * being shown yet.
     * @param controller The ParticleController for which the particles should be displayed
     */
    public abstract void updateParticleData(Camera cam, ParticleController controller);

}
