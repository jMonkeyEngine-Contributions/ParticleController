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
import com.jme3.math.ColorRGBA;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleInfluencer;
import java.io.IOException;

/**
 * The ColorInfluencer interpolates the particle from one color to another
 * over the course of its lifetime.
 */
public class ColorInfluencer implements ParticleInfluencer {

    private ColorRGBA startColor = new ColorRGBA().set(ColorRGBA.Red);
    private ColorRGBA endColor = new ColorRGBA().set(ColorRGBA.Yellow);

    /**
     * Sets the color the particle should be when emitted to the provided
     * ColorRGBA
     *
     * @param color The start color of the particle
     */
    public void setStartColor(ColorRGBA color) {
        this.startColor.set(color);
    }

    /**
     * Sets the color the particle should be when emitted to the provided r, g,
     * b, a values
     *
     * @param r The red value of the start color of the particle
     * @param g The green value of the start color of the particle
     * @param b The blue value of the start color of the particle
     * @param a The alpha value of the start color of the particle
     */
    public void setStartColor(float r, float g, float b, float a) {
        this.startColor.set(r, g, b, a);
    }

    /**
     * Return the start color of the particle
     *
     * @return The start ColorRGBA
     */
    public ColorRGBA getStartColor() {
        return this.startColor;
    }

    /**
     * Sets the color the particle should be when it finishes it's life cycle to
     * the provided ColorRGBA
     *
     * @param color The end color of the particle
     */
    public void setEndColor(ColorRGBA color) {
        this.endColor.set(color);
    }

    /**
     * Sets the color the particle should be when it finishes it's life cycle to
     * the provided r, g, b, a values
     *
     * @param r The red value of the end color of the particle
     * @param g The green value of the end color of the particle
     * @param b The blue value of the end color of the particle
     * @param a The alpha value of the end color of the particle
     */
    public void setEndColor(float r, float g, float b, float a) {
        this.endColor.set(r, g, b, a);
    }

    /**
     * Returns the end color of the particle
     *
     * @return The end ColorRGBA
     */
    public ColorRGBA getEndColor() {
        return this.endColor;
    }

    /**
     * This constructor creates a new ColorInfluencer with the given start and end
     * colors.
     * 
     * @param startColor The initial color of the particles
     * @param endColor The final color of the particles
     */
    public ColorInfluencer(ColorRGBA startColor, ColorRGBA endColor) {
        this.startColor.set(startColor);
        this.endColor.set(endColor);
    }
    

    @Override
    protected Object clone() throws CloneNotSupportedException {
        ColorInfluencer clone = (ColorInfluencer)super.clone();
        clone.startColor = new ColorRGBA(startColor);
        clone.endColor = new ColorRGBA(endColor);
        return clone;
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(startColor, "startColor", ColorRGBA.Red);
        oc.write(endColor, "endColor", ColorRGBA.Yellow);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        startColor = (ColorRGBA) ic.readSavable("startColor", ColorRGBA.Red.clone());
        endColor = (ColorRGBA) ic.readSavable("endColor", ColorRGBA.Yellow.clone());
    }

    @Override
    public void influenceParticleCreation(ParticleController ctrl, int index, ParticleData data) {
        data.color.set(startColor);
    }

    @Override
    public void influenceParticle(ParticleController ctrl, int index, ParticleData data, float tpf) {
        data.color.interpolate(startColor, endColor, data.lifeProgress);
    }

    @Override
    public ParticleInfluencer cloneForController(ParticleController controller) {
        return new ColorInfluencer(startColor, endColor);
    }
}
