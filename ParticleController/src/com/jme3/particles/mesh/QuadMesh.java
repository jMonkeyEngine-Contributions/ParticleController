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

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleMesh;
import com.jme3.renderer.Camera;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * This class implements QuadMesh and offers a point-sprite based particle system.
 * 
 * This is generally less efficient than PointMesh as four vertices are required for
 * each particle, but is much more flexible. There are no limitations to the size
 * each particle can fill on the screen and the full range of billboarding options
 * are available.
 */
public class QuadMesh extends ParticleMesh {

    private final QuadMeshBillboardStrategy billboardStrategy;
    
    private static final Vector3f up = new Vector3f();
    private static final Vector3f left = new Vector3f();
    private static final Vector3f dir = new Vector3f();
    
    /**
     * Construct a new QuadMesh using the supplied QuadMeshBillboardStrategy, and
     * that will load the sprite texture from the given path using the supplied 
     * AssetManager.
     * 
     * @param billboardStrategy The QuadMeshBillboardStrategy to use
     * @param assetManager The AssetManager to use
     * @param texturePath The path from which to load the texture used for particles
     */
    public QuadMesh(QuadMeshBillboardStrategy billboardStrategy, AssetManager assetManager, String texturePath) {
        super(new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md"), 1, 1);
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        material.setTexture("Texture", assetManager.loadTexture(texturePath));
        this.billboardStrategy = billboardStrategy;
    }

    /**
     * Construct a new QuadMesh using the supplied QuadMeshBillboardStrategy, and
     * that will load the sprite texture from the given path using the supplied 
     * AssetManager.
     * 
     * @param billboardStrategy The QuadMeshBillboardStrategy to use
     * @param assetManager The AssetManager to use
     * @param texturePath The path from which to load the texture used for particles
     * @param spriteCols The number of columns of sprites in the texture
     * @param spriteRows The number of rows of sprites in the texture
     */
    public QuadMesh(QuadMeshBillboardStrategy billboardStrategy, AssetManager assetManager, String texturePath, int spriteCols, int spriteRows) {
        super(new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md"), spriteCols, spriteRows);
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        material.setTexture("Texture", assetManager.loadTexture(texturePath));
        this.billboardStrategy = billboardStrategy;
    }
    
     /**
     * Construct a new QuadMesh using the supplied QuadMeshBillboardStrategy, and
     * which will use the supplied material
     * 
     * @param billboardStrategy The QuadMeshBillboardStrategy to use
     * @param material The material to use
     * @param spriteCols The number of columns of sprites in the texture
     * @param spriteRows The number of rows of sprites in the texture
     */
   public QuadMesh(QuadMeshBillboardStrategy billboardStrategy, Material material, int spriteCols, int spriteRows) {
        super(material, spriteCols, spriteRows);
        this.billboardStrategy = billboardStrategy;
    }

    @Override
    public void initializeParticleData(ParticleController controller) {

        int numParticles = controller.getMaxParticles();

        // set positions
        FloatBuffer pb = BufferUtils.createVector3Buffer(numParticles * 4);
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

        // set colors
        ByteBuffer cb = BufferUtils.createByteBuffer(numParticles * 4 * 4);
        buf = getBuffer(VertexBuffer.Type.Color);
        if (buf != null) {
            buf.updateData(cb);
        } else {
            VertexBuffer cvb = new VertexBuffer(VertexBuffer.Type.Color);
            cvb.setupData(VertexBuffer.Usage.Stream, 4, VertexBuffer.Format.UnsignedByte, cb);
            cvb.setNormalized(true);
            setBuffer(cvb);
        }

        // set texcoords
        FloatBuffer tb = BufferUtils.createVector2Buffer(numParticles * 4);
        for (int i = 0; i < numParticles; i++) {
            tb.put(0f).put(1f);
            tb.put(1f).put(1f);
            tb.put(0f).put(0f);
            tb.put(1f).put(0f);
        }
        tb.flip();

        buf = getBuffer(VertexBuffer.Type.TexCoord);
        if (buf != null) {
            buf.updateData(tb);
        } else {
            buf = new VertexBuffer(VertexBuffer.Type.TexCoord);
            buf.setupData(uniqueTexCoords? VertexBuffer.Usage.Dynamic: VertexBuffer.Usage.Static, 2, VertexBuffer.Format.Float, tb);
            setBuffer(buf);
        }
        
        if (!uniqueTexCoords) {
            FloatBuffer texcoords = (FloatBuffer) buf.getData();
            
            for (int i = 0; i < numParticles; i++) {
                texcoords.put(0).put(1);
                texcoords.put(1).put(1);
                texcoords.put(0).put(0);
                texcoords.put(1).put(0);
            }
        }

        // set indices
        ShortBuffer ib = BufferUtils.createShortBuffer(numParticles * 6);
        for (int i = 0; i < numParticles; i++) {
            int startIdx = (i * 4);

            // triangle 1
            ib.put((short) (startIdx + 1))
                    .put((short) (startIdx + 0))
                    .put((short) (startIdx + 2));

            // triangle 2
            ib.put((short) (startIdx + 1))
                    .put((short) (startIdx + 2))
                    .put((short) (startIdx + 3));
        }
        ib.flip();

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

        VertexBuffer cvb = getBuffer(VertexBuffer.Type.Color);
        ByteBuffer colors = (ByteBuffer) cvb.getData();

        VertexBuffer tvb = getBuffer(VertexBuffer.Type.TexCoord);
        FloatBuffer texcoords = (FloatBuffer) tvb.getData();

        // update data in vertex buffers
        positions.clear();
        colors.clear();
        if (uniqueTexCoords) {
            texcoords.clear();
        }
        
        ParticleData[] particles = controller.getParticles();

        for (int i = 0; i < particles.length; i++) {
            ParticleData p = particles[i];

            if (p.active) {

                if (cam != null) {
                    billboardStrategy.billboard(cam, controller, p, up, left, dir);
                } else {
                    // Fall back to fixed orientation until camera information is available
                    QuadMeshBillboardStrategy.UNIT_Y.billboard(cam, controller, p, up, left, dir);
                }
                up.multLocal(p.size);
                left.multLocal(p.size);

                positions.put(p.position.x + left.x + up.x)
                        .put(p.position.y + left.y + up.y)
                        .put(p.position.z + left.z + up.z);

                positions.put(p.position.x - left.x + up.x)
                        .put(p.position.y - left.y + up.y)
                        .put(p.position.z - left.z + up.z);

                positions.put(p.position.x + left.x - up.x)
                        .put(p.position.y + left.y - up.y)
                        .put(p.position.z + left.z - up.z);

                positions.put(p.position.x - left.x - up.x)
                        .put(p.position.y - left.y - up.y)
                        .put(p.position.z - left.z - up.z);

                if (uniqueTexCoords) {
                    float imgX = p.spriteCol;
                    float imgY = p.spriteRow;

                    float startX = imgX / spriteCols;
                    float startY = imgY / spriteRows;
                    float endX = startX + 1f / spriteCols;
                    float endY = startY + 1f / spriteRows;

                    texcoords.put(startX).put(endY);
                    texcoords.put(endX).put(endY);
                    texcoords.put(startX).put(startY);
                    texcoords.put(endX).put(startY);
                }
                
                int abgr = p.color.asIntABGR();
                colors.putInt(abgr);
                colors.putInt(abgr);
                colors.putInt(abgr);
                colors.putInt(abgr);
            } else {
                positions.put(0)
                        .put(0)
                        .put(0);

                positions.put(0)
                        .put(0)
                        .put(0);

                positions.put(0)
                        .put(0)
                        .put(0);

                positions.put(0)
                        .put(0)
                        .put(0);
                
                colors.putInt(0);
                colors.putInt(0);
                colors.putInt(0);
                colors.putInt(0);
                
                if (uniqueTexCoords) {
                    texcoords.put(0).put(1);
                    texcoords.put(1).put(1);
                    texcoords.put(0).put(0);
                    texcoords.put(1).put(0);
                }
            }

        }

        positions.flip();
        colors.flip();
        
        if (uniqueTexCoords) {
            texcoords.flip();
            tvb.updateData(texcoords);
        }

        // force renderer to re-send data to GPU
        pvb.updateData(positions);
        cvb.updateData(colors);
        
        updateCounts();
    }
}
