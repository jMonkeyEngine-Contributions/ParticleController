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
package com.jme3.particles.mesh;

import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.renderer.Camera;

/**
 * The QuadMeshBillboardStrategy controls how particles are displayed by modifying
 * the orientation of the quad appropriately. Most of the common requirements are
 * supplied but if you have a specialist need then you can implement your own
 * strategy to give exactly the behaviour you require.
 */
public abstract class QuadMeshBillboardStrategy {

    private static final Quaternion workingQ = new Quaternion();

    /**
     * Called by the QuadMesh to update each particle
     * 
     * @param cam The camera on which the particle is being displayed
     * @param p The data for the particle being displayed
     * @param up Store into this the desired up vector for the particle
     * @param left Store into this the desired left vector for the particle
     * @param dir Store into this the desired direction vector for the particle
     */
    public abstract void billboard(Camera cam, ParticleController ctrlr, ParticleData p, Vector3f up, Vector3f left, Vector3f dir);
    
    /**
     * This billboards all particles in their current direction of travel, with Y axis up
     */
    public static final QuadMeshBillboardStrategy VELOCITY = new QuadMeshBillboardStrategy() {

        @Override
        public void billboard(Camera cam, ParticleController ctrlr, ParticleData p, Vector3f up, Vector3f left, Vector3f dir) {
            up.set(p.velocity).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
            left.set(p.velocity).crossLocal(up).normalizeLocal();
            dir.set(p.velocity);
        }

    };
    
    /**
     * This billboards all particles in their current direction of travel, with Z axis up
     */
    public static final QuadMeshBillboardStrategy VELOCITY_Z_UP = new QuadMeshBillboardStrategy() {

        @Override
        public void billboard(Camera cam, ParticleController ctrlr, ParticleData p, Vector3f up, Vector3f left, Vector3f dir) {
                up.set(p.velocity).crossLocal(Vector3f.UNIT_Y).normalizeLocal();
                left.set(p.velocity).crossLocal(up).normalizeLocal();
                dir.set(p.velocity);
                workingQ.fromAngleAxis(-90*FastMath.DEG_TO_RAD, left);
                workingQ.multLocal(left);
                workingQ.multLocal(up);
        }
    };
    
    /**
     * This uses the rotation stored in the particle and does not billboard at all. Use this if
     * you have rotating particles.
     */
    public static final QuadMeshBillboardStrategy USE_PARTICLE_ROTATION = new QuadMeshBillboardStrategy() {

        @Override
        public void billboard(Camera cam, ParticleController ctrlr, ParticleData p, Vector3f up, Vector3f left, Vector3f dir) {
            up.set(Vector3f.UNIT_Z);
            left.set(Vector3f.UNIT_X);
            dir.set(Vector3f.UNIT_Y);
            
            p.rotation.multLocal(up);
            p.rotation.multLocal(left);
            p.rotation.multLocal(dir);
            
        }
    };

    /**
     * This is the most common billboard strategy, and simply billboards all particles
     * towards the camera.
     */
    public static final QuadMeshBillboardStrategy CAMERA = new QuadMeshBillboardStrategy() {

        @Override
        public void billboard(Camera cam, ParticleController ctrlr, ParticleData p, Vector3f up, Vector3f left, Vector3f dir) {
            up.set(cam.getUp());
            left.set(cam.getLeft());
            dir.set(cam.getDirection());
        }
    };

    /**
     * This version of CAMERA Billboard compensates for rotation of the node to which the
     * Particle Geometry is attached. It performs more calculations than the standard
     * camera billboarding so should only be used when required.
     */
    public static final QuadMeshBillboardStrategy CAMERA_ROTATION_SAFE = new QuadMeshBillboardStrategy() {

        @Override
        public void billboard(Camera cam, ParticleController ctrlr, ParticleData p, Vector3f up, Vector3f left, Vector3f dir) {
            workingQ.set(ctrlr.getGeometry().getWorldRotation());
            workingQ.inverseLocal();
            workingQ.mult(cam.getUp(), up);
            workingQ.mult(cam.getLeft(), left);
            workingQ.mult(cam.getDirection(), dir);
        }
    };

    /**
     * This implementation of QuadMeshBillboardStrategy simply always returns the same
     * values no matter what inputs it is given. Note that as it is used as a fallback
     * strategy if no camera is available this class supports a null value for the
     * Camera parameter.
     */
    public static class StaticBillboardStrategy extends QuadMeshBillboardStrategy {

        private final Vector3f up;
        private final Vector3f left;
        private final Vector3f dir;

        /**
         * The up, left and direction vectors to always supply for billboard particles
         */
        public StaticBillboardStrategy(Vector3f up, Vector3f left, Vector3f dir) {
            this.up = up;
            this.left = left;
            this.dir = dir;
        }
        
        @Override
        public void billboard(Camera cam, ParticleController ctrlr, ParticleData p, Vector3f up, Vector3f left, Vector3f dir) {
            up.set(this.up);
            left.set(this.left);
            dir.set(this.dir);
        }
    }

    /**
     * Always billboard in the X direction.
     */
    public static final QuadMeshBillboardStrategy UNIT_X = new StaticBillboardStrategy(Vector3f.UNIT_Y, Vector3f.UNIT_Z, Vector3f.UNIT_X);

    /**
     * Always billboard in the Y direction
     */
    public static final QuadMeshBillboardStrategy UNIT_Y = new StaticBillboardStrategy(Vector3f.UNIT_Z, Vector3f.UNIT_X, Vector3f.UNIT_Y);

    /**
     * Always billboard in the Z direction
     */
    public static final QuadMeshBillboardStrategy UNIT_Z = new StaticBillboardStrategy(Vector3f.UNIT_X, Vector3f.UNIT_Y, Vector3f.UNIT_Z);
    
}
