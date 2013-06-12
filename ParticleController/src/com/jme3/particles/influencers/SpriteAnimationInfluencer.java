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
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleInfluencer;
import java.io.IOException;

/**
 * This influencer animates the particle over its life cycle. It always starts with
 * the first frame and moves along rows and then columns in flow order until it reaches
 * the final frame. The frame rate is specified here and also whether it should pause or
 * cycle when it reaches the final frame.
 */
public class SpriteAnimationInfluencer implements ParticleInfluencer {

    private boolean cycle;
    private float timePerFrame;
    private int firstFrame;
    private int lastFrame;

    /**
     * Construct a new SpriteAnimationInfluencer 
     * 
     * @param cycle Should the animation cycle or pause at the end
     * @param timePerFrame How long to stay on each frame before advancing
     * @param firstFrame The first frame to display
     * @param lastFrame The last frame to display
     */
    public SpriteAnimationInfluencer(boolean cycle, float timePerFrame, int firstFrame, int lastFrame) {
        this.cycle = cycle;
        this.timePerFrame = timePerFrame;
        this.firstFrame = firstFrame;
        this.lastFrame = lastFrame;
    }

    /**
     * Construct a new SpriteAnimationInfluencer 
     * 
     * @param cycle Should the animation cycle or pause at the end
     * @param framesPerSecond The number of frames to display in each second
     * @param firstFrame The first frame to display
     * @param lastFrame The last frame to display
     */
    public SpriteAnimationInfluencer(boolean cycle, int framesPerSecond, int firstFrame, int lastFrame) {
        this(cycle, 1f/framesPerSecond, firstFrame, lastFrame);
    }

    /**
     * @return Whether the animation will cycle or pause when it reaches the final frame
     */
    public boolean isCycle() {
        return cycle;
    }

    /**
     * @param cycle Set whether animations should cycle or pause when they reach the final frame
     */
    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    /**
     * @return Get the time between each frame of animation being displayed
     */
    public float getTimePerFrame() {
        return timePerFrame;
    }

    /**
     * @param timePerFrame Set the time between each frame of animation being displayed
     */
    public void setTimePerFrame(float timePerFrame) {
        this.timePerFrame = timePerFrame;
    }

    /**
     * @return The frame at which the animation will start (counting from top left
     * rows first)
     */
    public int getFirstFrame() {
        return firstFrame;
    }

    /**
     * @param firstFrame Set the frame at which animation will start
     */
    public void setFirstFrame(int firstFrame) {
        this.firstFrame = firstFrame;
    }

    /**
     * @return The frame at which animations will end
     */
    public int getLastFrame() {
        return lastFrame;
    }

    /**
     * @param lastFrame The frame at which animations will end
     */
    public void setLastFrame(int lastFrame) {
        this.lastFrame = lastFrame;
    }
    
    @Override
    public void influenceParticleCreation(ParticleController ctrl, int index, ParticleData data) {
        data.spriteCol = 0;
        data.spriteRow = 0;
    }

    @Override
    public void influenceParticle(ParticleController ctrl, int index, ParticleData data, float tpf) {
        int frameNumber = (int)((data.startlife - data.life)/timePerFrame);
        int cols = ctrl.getMesh().getSpriteCols();
        int limit = cols * ctrl.getMesh().getSpriteRows();
        if (cycle) {
            frameNumber %= limit;
        } else if (frameNumber >= limit) {
            frameNumber = limit-1;
        }
        data.spriteCol = frameNumber % cols;
        data.spriteRow = frameNumber / cols;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(cycle, "cycle", Boolean.TRUE);
        capsule.write(timePerFrame, "timePerFrame", 0);
        capsule.write(firstFrame, "firstFrame", 0);
        capsule.write(lastFrame, "lastFrame", 0);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        cycle = capsule.readBoolean("cycle", Boolean.TRUE);
        timePerFrame = capsule.readFloat("timePerFrame", 0);
        firstFrame = capsule.readInt("firstFrame", 0);
        lastFrame = capsule.readInt("lastFrame", 0);
    }

    @Override
    public ParticleInfluencer cloneForController(ParticleController controller) {
        return new SpriteAnimationInfluencer(cycle, timePerFrame, firstFrame, lastFrame);
    }
}
