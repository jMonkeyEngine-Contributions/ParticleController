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
import com.jme3.export.Savable;
import com.jme3.math.ColorRGBA;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleInfluencer;
import java.io.IOException;

/**
 * The MultiColorInfluencer causes particles to move through multiple different
 * colors at different stages through the particle life cycle.
 */
public class MultiColorInfluencer implements ParticleInfluencer {

    /**
     * Each Stage specifies a time within the particles life cycle and the Color
     * at which it should be at that time.
     */
    public static class Stage implements Savable {
        float time;
        ColorRGBA color;

        /**
         * Construct a new MultiColorInfluencer.Stage
         * 
         * @param time The time at which this stage should be reached (this goes
         * from 0 to 1 over the course of the particle life span - so 0 is at the
         * start, 0.5 half way through and 1 at the end.
         * @param color The color the particle will be at at this time
         */
        public Stage(float time, ColorRGBA color) {
            this.time = time;
            this.color = color;
        }

        @Override
        public void write(JmeExporter ex) throws IOException {
            OutputCapsule capsule = ex.getCapsule(this);
            capsule.write(time, "time", 0);
            capsule.write(color, "color", null);
        }

        @Override
        public void read(JmeImporter im) throws IOException {
            InputCapsule capsule = im.getCapsule(this);
            time = capsule.readFloat("time", time);
            color = (ColorRGBA) capsule.readSavable("color", null);
        }
    }
    
    private Stage[] stages;

    /**
     * This method returns the array of stages being used.
     * 
     * @return The array of stages being used by this MultiColorInfluencer
     */
    public Stage[] getStages() {
        return stages;
    }

    /**
     * This method changes the array of stages being used by this influencer,
     * it should only be used from the render thread but other than that can
     * safely be used while the particles are "live".
     * 
     * @param stages The new array of stages.
     */
    public void setStages(Stage... stages) {
        this.stages = stages;
    }

    /**
     * Construct a new MultiColorInfluencer with the specified list of stages.
     * 
     * @param stages The stages through which particles will transition.
     */
    public MultiColorInfluencer(Stage... stages) {
        this.stages = stages;
    }
    
    @Override
    public ParticleInfluencer cloneForController(ParticleController controller) {
        return new MultiColorInfluencer(stages);
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(stages, "stages", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        ic.readSavableArray("stages", null);
    }

    @Override
    public void influenceParticleCreation(ParticleController ctrl, int index, ParticleData data) {
        data.color.set(stages[0].color);
    }

    @Override
    public void influenceParticle(ParticleController ctrl, int index, ParticleData data, float tpf) {
        
        for (int i=0;i<stages.length;i++) {
            if (stages[i].time > data.lifeProgress) {
                if (i == 0) {
                    data.color.set(stages[i].color);
                } else {
                    data.color.interpolate(
                            stages[i-1].color, 
                            stages[i].color, 
                            (data.lifeProgress - stages[i-1].time)/(stages[i].time-stages[i-1].time));
                }
                return;
            }
        }
        data.color.set(stages[stages.length-1].color);
    }
}
