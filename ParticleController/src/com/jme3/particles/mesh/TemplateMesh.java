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

import com.jme3.material.Material;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleMesh;
import com.jme3.renderer.Camera;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * The TemplateMesh takes an arrow of templates, each of which is a mesh of its
 * own. It then uses a mesh selected from the templates to represent each particle.
 * 
 * The mesh to be used is specified in the spriteCol field so a RandomSpriteInfluencer
 * can be used to have a template selected at random for each particle as it is spawned.
 */
public class TemplateMesh extends ParticleMesh {

    private Mesh[] templates;
    private int largestMeshVertices;
    private int largestMeshTriangles;
    private boolean useVertexColors;
    private boolean useNormals;

    private static final Transform particleTransform = new Transform();
    private static final Vector3f working = new Vector3f();
    
    public TemplateMesh(Material material, boolean useVertexColors, boolean useNormals, Mesh... templates) {
        super(material, templates.length, 1);
        this.useVertexColors = useVertexColors;
        this.useNormals = useNormals;
        this.templates = templates;
        largestMeshVertices = 0;
        largestMeshTriangles = 0;
        
        for (Mesh m: templates) {
            int count = m.getVertexCount();
            if (count > largestMeshVertices) {
                largestMeshVertices = count;
            }
            count = m.getTriangleCount();
            if (count > largestMeshTriangles) {
                largestMeshTriangles = count;
            }
        }
    }
    
    @Override
    public void initializeParticleData(ParticleController controller) {

        int numParticles = controller.getMaxParticles();

        // set positions
        FloatBuffer pb = BufferUtils.createVector3Buffer(numParticles * largestMeshVertices);
        // if the buffer is already set only update the data
        VertexBuffer buf = getBuffer(VertexBuffer.Type.Position);
        if (buf != null) {
            buf.updateData(pb);
        } else {
            VertexBuffer pvb = new VertexBuffer(VertexBuffer.Type.Position);
            pvb.setupData(VertexBuffer.Usage.Stream, 3, VertexBuffer.Format.Float, pb);
            setBuffer(pvb);
        }
        pb.flip();

        if (useNormals) {
            // set positions
            FloatBuffer nb = BufferUtils.createVector3Buffer(numParticles * largestMeshVertices);
            // if the buffer is already set only update the data
            buf = getBuffer(VertexBuffer.Type.Normal);
            if (buf != null) {
                buf.updateData(nb);
            } else {
                VertexBuffer nvb = new VertexBuffer(VertexBuffer.Type.Normal);
                nvb.setupData(VertexBuffer.Usage.Stream, 3, VertexBuffer.Format.Float, nb);
                setBuffer(nvb);
            }
            nb.flip();
        }        
        
        // set colors
        if (useVertexColors) {
            ByteBuffer cb = BufferUtils.createByteBuffer(numParticles * 4 * largestMeshVertices);
            buf = getBuffer(VertexBuffer.Type.Color);
            if (buf != null) {
                buf.updateData(cb);
            } else {
                VertexBuffer cvb = new VertexBuffer(VertexBuffer.Type.Color);
                cvb.setupData(VertexBuffer.Usage.Stream, 4, VertexBuffer.Format.UnsignedByte, cb);
                cvb.setNormalized(true);
                setBuffer(cvb);
            }
        }
        
        // set texcoords
        FloatBuffer tb = BufferUtils.createVector2Buffer(numParticles * largestMeshVertices);
        tb.flip();

        buf = getBuffer(VertexBuffer.Type.TexCoord);
        if (buf != null) {
            buf.updateData(tb);
        } else {
            buf = new VertexBuffer(VertexBuffer.Type.TexCoord);
            buf.setupData(VertexBuffer.Usage.Dynamic, 2, VertexBuffer.Format.Float, tb);
            setBuffer(buf);
        }

        // set indices
        ShortBuffer ib = BufferUtils.createShortBuffer(numParticles * largestMeshTriangles * 3);

        buf = getBuffer(VertexBuffer.Type.Index);
        if (buf != null) {
            buf.updateData(ib);
        } else {
            VertexBuffer ivb = new VertexBuffer(VertexBuffer.Type.Index);
            ivb.setupData(VertexBuffer.Usage.Static, 3, VertexBuffer.Format.UnsignedShort, ib);
            setBuffer(ivb);
        }

        updateCounts();
    }

    @Override
    public void updateParticleData(Camera cam, ParticleController controller) {

        VertexBuffer pvb = getBuffer(VertexBuffer.Type.Position);
        FloatBuffer positions = (FloatBuffer) pvb.getData();

        VertexBuffer cvb = null;
        ByteBuffer colors = null;
        
        if (useVertexColors) {
            cvb = getBuffer(VertexBuffer.Type.Color);
            colors = (ByteBuffer) cvb.getData();
        }

        VertexBuffer nvb = null;
        FloatBuffer normals = null;

        if (useNormals) {
            nvb = getBuffer(VertexBuffer.Type.Normal);
            normals = (FloatBuffer) nvb.getData();
        }
        
        VertexBuffer tvb = getBuffer(VertexBuffer.Type.TexCoord);
        FloatBuffer texcoords = (FloatBuffer) tvb.getData();

        VertexBuffer ib = getBuffer(VertexBuffer.Type.Index);
        ShortBuffer indexes = (ShortBuffer) ib.getData();
        
        // update data in vertex buffers
        positions.clear();
        if (useVertexColors) {
            colors.clear();
        }
        if (useNormals) {
            normals.clear();
        }
        texcoords.clear();
        indexes.clear();
        
        ParticleData[] particles = controller.getParticles();

        for (int i = 0; i < particles.length; i++) {
            ParticleData p = particles[i];

            if (p.active) {

                particleTransform.setTranslation(p.position);
                particleTransform.setRotation(p.rotation);
                particleTransform.setScale(p.size);
                
                int startIndex = positions.position()/3;
                Mesh template = templates[p.spriteCol];
                FloatBuffer sourcePos = template.getFloatBuffer(VertexBuffer.Type.Position);
                sourcePos.rewind();
                
                while (sourcePos.hasRemaining()) {
                    working.set(sourcePos.get(), sourcePos.get(), sourcePos.get());
                    particleTransform.transformVector(working, working);
                    positions.put(working.x).put(working.y).put(working.z);
                }
                
                if (useVertexColors) {
                    ByteBuffer sourceCol = (ByteBuffer)template.getBuffer(VertexBuffer.Type.Color).getData();
                    sourceCol.rewind();
                    colors.put(sourceCol);
                }

                if (useNormals) {
                    FloatBuffer sourceNorm = template.getFloatBuffer(VertexBuffer.Type.Normal);
                    sourceNorm.rewind();

                    while (sourceNorm.hasRemaining()) {
                        working.set(sourceNorm.get(), sourceNorm.get(), sourceNorm.get());
                        p.rotation.mult(working, working);
                        normals.put(working.x).put(working.y).put(working.z);
                    }
                }
                
                FloatBuffer sourceTex = template.getFloatBuffer(VertexBuffer.Type.TexCoord);
                sourceTex.rewind();
                texcoords.put(sourceTex);
                
                int newIndex = 0;
                int pos = positions.position();
                
                ShortBuffer sourceIndex = template.getShortBuffer(VertexBuffer.Type.Index);
                sourceIndex.rewind();
                while (sourceIndex.hasRemaining()) {
                    newIndex = sourceIndex.get() + startIndex;
                    if (newIndex >= Short.MAX_VALUE) {
                        throw new IllegalStateException("TemplateMesh index count rose higher than can fit in a short!");
                    }
                    if (newIndex >= pos) {
                        throw new IllegalStateException("Template "+p.spriteCol+" triangle index "+(newIndex-startIndex)+" outside triangle bounds");
                    }
                    indexes.put((short)newIndex);
                }
            }

        }
        
        positions.mark();
        while (positions.hasRemaining()) {
            positions.put(0).put(0).put(0);
        }

        positions.reset();
        positions.flip();
        pvb.updateData(positions);

        indexes.mark();
        while (indexes.hasRemaining()) {
            indexes.put((short)0).put((short)0).put((short)0);
        }
        indexes.reset();
        indexes.flip();
        ib.updateData(indexes);
        
        if (useVertexColors) {
            colors.flip();
            cvb.updateData(colors);
        }
        
        if (useNormals) {
            normals.flip();
            nvb.updateData(normals);
        }
        
        texcoords.flip();
        tvb.updateData(texcoords);

        
        updateCounts();
    }
    
}
