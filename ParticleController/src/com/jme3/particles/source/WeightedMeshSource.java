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
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleSource;
import com.jme3.scene.Geometry;
import java.io.IOException;

/**
 * Generates particles from the supplied mesh. A triangle is selected at random
 * and then a random point within that triangle. The particle is then generated
 * with velocity equal to the normal of that face. Note that the normal just
 * looks at the triangle and does not use any information that might be in
 * vertex buffers or normal maps.
 * 
 * This differs from MeshSource in that it scans the geometry and caches the
 * size of each triangle when is is constructed. It then uses those cached values
 * to weight triangle selection so that you have an equal chance to emit a particle
 * from any point on the mesh. Without this areas within large triangles have a
 * smaller chance to emit than areas within small triangles.
 * 
 * The values are scanned and cached both when this is attached and when the method
 * recalculateWeights is called. They are not updated otherwise but in most cases
 * will still be better than a MeshSource even on an animated mesh.
 * 
 * This class will use more memory and CPU time than a MeshSource so should only 
 * be used when required.
 */
public class WeightedMeshSource implements ParticleSource {

    private Geometry geometry;
    private final Triangle triStore = new Triangle();
    private final Vector3f origin = new Vector3f();
    private final Vector3f side1 = new Vector3f();
    private final Vector3f side2 = new Vector3f();
    
    private float[] weights;

    /**
     * Generate a new mesh source for the supplied geometry.
     * 
     * @param geometry The geometry to emit particles from
     */
    public WeightedMeshSource(Geometry geometry) {
        this.geometry = geometry;
        recalculateWeights();
    }
    
    /**
     * Recalculate the weights given to each triangle. This should be called whenever
     * the shape of triangles changes significantly and <b>must</b> be called whenever
     * the number of triangles changes.
     */
    public final void recalculateWeights() {
        weights = new float[geometry.getMesh().getTriangleCount()];
        float totalWeight = 0;
        for (int i=0;i<weights.length;i++) {
            geometry.getMesh().getTriangle(i, triStore);
            origin.set(triStore.get1());
            side1.set(triStore.get2()).subtractLocal(origin);
            side2.set(triStore.get3()).subtractLocal(origin);
            
            // This will calculate double the area for all the triangles, since we are
            // looking at proportional difference this *2 will cancel out so no need to divide
            weights[i] = side1.length() + side2.length();
            totalWeight += weights[i];
        }
        
        for (int i=0;i<weights.length;i++) {
            weights[i] /= totalWeight;
        }
    }
    
    
    @Override
    public void sourceParticle(ParticleController pCtrl, int index, ParticleData particle) {
        
        float selector = FastMath.nextRandomFloat();
        float totalWeight = 0;
        int triangleIndex = -1;
        while (totalWeight < selector) {
            triangleIndex ++;
            totalWeight += weights[triangleIndex];
        }
        
        geometry.getMesh().getTriangle(triangleIndex, triStore);
        
        particle.velocity.set(triStore.getNormal());
        
        origin.set(triStore.get1());
        side1.set(triStore.get2()).subtractLocal(origin);
        side2.set(triStore.get3()).subtractLocal(origin);
        
        float d1 = FastMath.nextRandomFloat();
        float d2 = FastMath.nextRandomFloat();
        
        if (d1 + d2 > 1) {
            d1 = 1-d1;
            d2 = 1-d2;
        }
        
        side1.multLocal(d1);
        side2.multLocal(d2);
        
        origin.addLocal(side1).addLocal(side2);
        
        geometry.getWorldTransform().transformVector(origin, origin);
        pCtrl.getGeometry().getWorldTransform().transformInverseVector(origin, particle.position);
    }
    

    @Override
    public ParticleSource cloneForController(ParticleController ctrlr) {
        return new WeightedMeshSource(geometry);
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        ex.getCapsule(this).write(geometry, "geometry", null);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        geometry = (Geometry) im.getCapsule(this).readSavable("geometry", null);
    }}
