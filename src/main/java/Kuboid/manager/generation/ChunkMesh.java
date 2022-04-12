package Kuboid.manager.generation;

import Kuboid.manager.model.VoxelModel;
import Kuboid.manager.voxel.Vertex;
import Kuboid.manager.voxel.Voxel;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class ChunkMesh {

    private List<Vertex> vertices;
    private List<Vector3f> usedPos;

    private List<Float> positionsList;
    private List<Float> normalsList;
    private List<Float> uvsList;

    public float[] positions, normals, uvs;

    public Chunk chunk;

    public ChunkMesh(Chunk chunk) {
        vertices = new ArrayList<>();
        usedPos = new ArrayList<>();
        positionsList = new ArrayList<>();
        normalsList = new ArrayList<>();
        uvsList = new ArrayList<>();

        update(chunk);
    }

    public void update(Chunk chunk) {
        this.chunk = chunk;

        usedPos = getUsedPositions(chunk.getVoxels());

        buildMesh();
        populateList();
    }

    private List<Vector3f> getUsedPositions(List<Voxel> blocks) {
        List<Vector3f> auxList = new ArrayList<>();

        for (int i = 0; i < blocks.size(); i++) {
            auxList.add(blocks.get(i).origin);
        }

        return auxList;
    }

    private void buildMesh() {

        for (int i = 0; i < chunk.getVoxels().size(); i++) {
            Voxel voxelI = chunk.getVoxels().get(i);

            boolean px = false, nx = false, py = false, ny = false, pz = false, nz = false;

            //PX
            if (usedPos.contains(new Vector3f(voxelI.origin.x + 1, voxelI.origin.y, voxelI.origin.z))) {
                px = true;
            }

            //NX
            if (usedPos.contains(new Vector3f(voxelI.origin.x - 1, voxelI.origin.y, voxelI.origin.z))) {
                nx = true;
            }

            //PY
            if (usedPos.contains(new Vector3f(voxelI.origin.x, voxelI.origin.y + 1, voxelI.origin.z))) {
                py = true;
            }

            //NY
            if (usedPos.contains(new Vector3f(voxelI.origin.x, voxelI.origin.y - 1, voxelI.origin.z))) {
                ny = true;
            }

            //PZ
            if (usedPos.contains(new Vector3f(voxelI.origin.x, voxelI.origin.y, voxelI.origin.z + 1))) {
                pz = true;
            }

            //NZ
            if (usedPos.contains(new Vector3f(voxelI.origin.x, voxelI.origin.y, voxelI.origin.z - 1))) {
                nz = true;
            }

            /*for (int j = 0; j < chunk.getVoxels().size(); j++) {

                Voxel voxelJ = chunk.getVoxels().get(j);

                //PX
                if (((voxelI.origin.x + 1) == voxelJ.origin.x) && ((voxelI.origin.y) == (voxelJ.origin.y)) && ((voxelI.origin.z) == (voxelJ.origin.z))) {
                    px = true;
                }

                //NX
                if (((voxelI.origin.x - 1) == voxelJ.origin.x) && ((voxelI.origin.y) == (voxelJ.origin.y)) && ((voxelI.origin.z) == (voxelJ.origin.z))) {
                    nx = true;
                }

                //PY
                if (((voxelI.origin.y + 1) == voxelJ.origin.y) && ((voxelI.origin.x) == (voxelJ.origin.x)) && ((voxelI.origin.z) == (voxelJ.origin.z))) {
                    py = true;
                }

                //NY
                if (((voxelI.origin.y - 1) == voxelJ.origin.y) && ((voxelI.origin.x) == (voxelJ.origin.x)) && ((voxelI.origin.z) == (voxelJ.origin.z))) {
                    ny = true;
                }

                //PZ
                if (((voxelI.origin.z + 1) == voxelJ.origin.z) && ((voxelI.origin.y) == (voxelJ.origin.y)) && ((voxelI.origin.x) == (voxelJ.origin.x))) {
                    pz = true;
                }

                //NZ
                if (((voxelI.origin.z - 1) == voxelJ.origin.z) && ((voxelI.origin.y) == (voxelJ.origin.y)) && ((voxelI.origin.x) == (voxelJ.origin.x))) {
                    nz = true;
                }
            }*/

            if (!px) {
                for (int k = 0; k < 6; k++) {
                    vertices.add(new Vertex(new Vector3f(VoxelModel.PX_POS[k].x + voxelI.origin.x, VoxelModel.PX_POS[k].y + voxelI.origin.y, VoxelModel.PX_POS[k].z + voxelI.origin.z),
                            VoxelModel.NORMALS[k],
                            VoxelModel.UV[k]));
                }
            }

            if (!nx) {
                for (int k = 0; k < 6; k++) {
                    vertices.add(new Vertex(new Vector3f(VoxelModel.NX_POS[k].x + voxelI.origin.x, VoxelModel.NX_POS[k].y + voxelI.origin.y, VoxelModel.NX_POS[k].z + voxelI.origin.z),
                            VoxelModel.NORMALS[k],
                            VoxelModel.UV[k]));
                }
            }

            if (!py) {
                for (int k = 0; k < 6; k++) {
                    vertices.add(new Vertex(new Vector3f(VoxelModel.PY_POS[k].x + voxelI.origin.x, VoxelModel.PY_POS[k].y + voxelI.origin.y, VoxelModel.PY_POS[k].z + voxelI.origin.z),
                            VoxelModel.NORMALS[k],
                            VoxelModel.UV[k]));
                }
            }

            if (!ny) {
                for (int k = 0; k < 6; k++) {
                    vertices.add(new Vertex(new Vector3f(VoxelModel.NY_POS[k].x + voxelI.origin.x, VoxelModel.NY_POS[k].y + voxelI.origin.y, VoxelModel.NY_POS[k].z + voxelI.origin.z),
                            VoxelModel.NORMALS[k],
                            VoxelModel.UV[k]));
                }
            }

            if (!pz) {
                for (int k = 0; k < 6; k++) {
                    vertices.add(new Vertex(new Vector3f(VoxelModel.PZ_POS[k].x + voxelI.origin.x, VoxelModel.PZ_POS[k].y + voxelI.origin.y, VoxelModel.PZ_POS[k].z + voxelI.origin.z),
                            VoxelModel.NORMALS[k],
                            VoxelModel.UV[k]));
                }
            }

            if (!nz) {
                for (int k = 0; k < 6; k++) {
                    vertices.add(new Vertex(new Vector3f(VoxelModel.NZ_POS[k].x + voxelI.origin.x, VoxelModel.NZ_POS[k].y + voxelI.origin.y, VoxelModel.NZ_POS[k].z + voxelI.origin.z),
                            VoxelModel.NORMALS[k],
                            VoxelModel.UV[k]));
                }
            }
        }
    }

    private void populateList() {

        for (int i = 0; i < vertices.size(); i++) {
            Vertex vertex = vertices.get(i);

            positionsList.add(vertex.position.x);
            positionsList.add(vertex.position.y);
            positionsList.add(vertex.position.z);

            uvsList.add(vertex.uvs.x);
            uvsList.add(vertex.uvs.y);

            normalsList.add(vertex.normals.x);
            normalsList.add(vertex.normals.y);
            normalsList.add(vertex.normals.z);
        }

        positions = new float[positionsList.size()];
        normals = new float[normalsList.size()];
        uvs = new float[uvsList.size()];

        for (int i = 0; i < positionsList.size(); i++) {
            positions[i] = positionsList.get(i);
        }

        for (int i = 0; i < normalsList.size(); i++) {
            normals[i] = normalsList.get(i);
        }

        for (int i = 0; i < uvsList.size(); i++) {
            uvs[i] = uvsList.get(i);
        }

    }
}
