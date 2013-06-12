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
import com.jme3.particles.ParticleController;
import com.jme3.particles.ParticleData;
import com.jme3.particles.ParticleMesh;
import com.jme3.renderer.Camera;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

/**
 * This class implements ParticleMesh and offers a point-sprite based particle system.
 * 
 * This is generally more efficient than other approaches but will fall foul of the limitations
 * of point sprites. In particular some graphics cards limit the space on the screen that
 * one point sprite can occupy to a certain maximum pixel-count and also your billboard and 
 * rotation options are  non-existent.
 */
public class PointMesh extends ParticleMesh {


    /**
     * Construct a new PointMesh that will load the sprite texture from the given
     * path using the supplied AssetManager.
     * 
     * @param assetManager The AssetManager to use.
     * @param texturePath The path of the sprite texture.
     */
    public PointMesh(AssetManager assetManager, String texturePath) {
        super(new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md"), 1, 1);
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        material.setTexture("Texture", assetManager.loadTexture(texturePath));
        material.setBoolean("PointSprite", true);
    }

    /**
     * Construct a new PointMesh that will load the sprite texture from the given
     * path using the supplied AssetManager.
     * 
     * @param assetManager The AssetManager to use.
     * @param texturePath The path of the sprite texture.
     * @param spriteCols The number of columns of sprites in the texture
     * @param spriteRows The number of rows of sprites in the texture
     */
    public PointMesh(AssetManager assetManager, String texturePath, int spriteCols, int spriteRows) {
        super(new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md"), spriteCols, spriteRows);
        material.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        material.setTexture("Texture", assetManager.loadTexture(texturePath));
        material.setBoolean("PointSprite", true);
    }

    /**
     * Construct a new PointMesh which will use the supplied material
     * 
     * @param material The material to use
     */
    public PointMesh(Material material) {
        super(material, 1, 1);
    }

    /**
     * Construct a new PointMesh which will use the supplied material
     * 
     * @param material The material to be used on the geometry.
     * @param spriteCols The number of columns of sprites in the texture
     * @param spriteRows The number of rows of sprites in the texture
     */
    public PointMesh(Material material, int spriteCols, int spriteRows) {
        super(material, spriteCols, spriteRows);
    }
    
    
    @Override
    public void updateParticleData(Camera cam, ParticleController controller) {

        if (cam != null) {
            float C = cam.getProjectionMatrix().m00;
            C *= cam.getWidth() * 0.5f;

            material.setFloat("Quadratic", C);
        }
        
        ParticleData[] particles = controller.getParticles();

        VertexBuffer pvb = getBuffer(VertexBuffer.Type.Position);
        FloatBuffer positions = (FloatBuffer) pvb.getData();

        VertexBuffer cvb = getBuffer(VertexBuffer.Type.Color);
        ByteBuffer colors = (ByteBuffer) cvb.getData();

        VertexBuffer svb = getBuffer(VertexBuffer.Type.Size);
        FloatBuffer sizes = (FloatBuffer) svb.getData();

        VertexBuffer tvb = getBuffer(VertexBuffer.Type.TexCoord);
        FloatBuffer texcoords = (FloatBuffer) tvb.getData();

        // update data in vertex buffers
        positions.rewind();
        positions.limit(positions.capacity());
        colors.rewind();
        colors.limit(colors.capacity());
        sizes.rewind();
        sizes.limit(sizes.capacity());
        texcoords.rewind();
        texcoords.limit(texcoords.capacity());
        
        for (int i = 0; i < particles.length; i++){
            ParticleData p = particles[i];
            
            if (p.active) {

                positions.put(p.position.x)
                         .put(p.position.y)
                         .put(p.position.z);

                sizes.put(p.size);
                colors.putInt(p.color.asIntABGR());

                float imgX = p.spriteCol;
                float imgY = p.spriteRow;

                float startX = imgX / spriteCols;
                float startY = imgY / spriteRows;
                float endX   = startX + (1f / spriteCols);
                float endY   = startY + (1f / spriteRows);

                texcoords.put(startX).put(startY).put(endX).put(endY);
            }
        }
        
        // We haven't put inactive particles into the buffers, flip marks the limits
        // on the buffers so only the active particles get used.
        positions.flip();
        colors.flip();
        sizes.flip();
        texcoords.flip();

        // force renderer to re-send data to GPU
        pvb.updateData(positions);
        cvb.updateData(colors);
        svb.updateData(sizes);
        tvb.updateData(texcoords);
        
        // Update the vertex count
        updateCounts();
    }    

    @Override
    public void initializeParticleData(ParticleController controller) {
        int numParticles = controller.getMaxParticles();
        
        setMode(Mesh.Mode.Points);

        // set positions
        FloatBuffer pb = BufferUtils.createVector3Buffer(numParticles);
        
        //if the buffer is already set only update the data
        VertexBuffer buf = getBuffer(VertexBuffer.Type.Position);
        if (buf != null) {
            buf.updateData(pb);
        } else {
            VertexBuffer pvb = new VertexBuffer(VertexBuffer.Type.Position);
            pvb.setupData(VertexBuffer.Usage.Stream, 3, VertexBuffer.Format.Float, pb);
            setBuffer(pvb);
        }

        // set colors
        ByteBuffer cb = BufferUtils.createByteBuffer(numParticles * 4);
        
        buf = getBuffer(VertexBuffer.Type.Color);
        if (buf != null) {
            buf.updateData(cb);
        } else {
            VertexBuffer cvb = new VertexBuffer(VertexBuffer.Type.Color);
            cvb.setupData(VertexBuffer.Usage.Stream, 4, VertexBuffer.Format.UnsignedByte, cb);
            cvb.setNormalized(true);
            setBuffer(cvb);
        }

        // set sizes
        FloatBuffer sb = BufferUtils.createFloatBuffer(numParticles);
        
        buf = getBuffer(VertexBuffer.Type.Size);
        if (buf != null) {
            buf.updateData(sb);
        } else {
            VertexBuffer svb = new VertexBuffer(VertexBuffer.Type.Size);
            svb.setupData(VertexBuffer.Usage.Stream, 1, VertexBuffer.Format.Float, sb);
            setBuffer(svb);
        }

        // set UV-scale
        FloatBuffer tb = BufferUtils.createFloatBuffer(numParticles*4);
        
        buf = getBuffer(VertexBuffer.Type.TexCoord);
        if (buf != null) {
            buf.updateData(tb);
        } else {
            VertexBuffer tvb = new VertexBuffer(VertexBuffer.Type.TexCoord);
            tvb.setupData(VertexBuffer.Usage.Stream, 4, VertexBuffer.Format.Float, tb);
            setBuffer(tvb);
        }
        
        updateCounts();
    }
}
