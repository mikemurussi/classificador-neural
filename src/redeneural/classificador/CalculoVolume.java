package redeneural.classificador;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import redeneural.model.Classe;

/**
 *
 * @author Michael Murussi
 */
public class CalculoVolume implements Runnable {

    public final class Volume {
        
        private final Classe classe;
        protected int voxels;
        protected double volume;

        public Volume(Classe classe) {
            this.classe = classe;
            this.voxels = 0;
            this.volume = 0;
        }

        public Classe getClasse() {
            return classe;
        }

        public int getVoxels() {
            return voxels;
        }

        public double getVolume() {
            return volume;
        }

    }

    private Status status;
    private final Volume[] volumes;
    private final File[] arquivos;
    private final double[] resolucaoVoxel;

    /**
     * Contrutor de CalculoVolume
     *
     * @param classes
     * @param arquivos Lista dos arquivos do volume
     * @param resX Resolução X do voxel
     * @param resY Resolução Y do voxel (corresponde à espessura das fatias)
     * @param resZ Resolução Z do voxel
     */
    public CalculoVolume(List<Classe> classes, File[] arquivos, double resX, double resY, double resZ) {

        this.volumes = new Volume[classes.size()];
        int i = 0;
        for (Classe c: classes) {
            this.volumes[i++] = new Volume(c);
        }
        this.arquivos = arquivos;
        this.resolucaoVoxel = new double[]{resX, resY, resZ};
    }

    @Override
    public void run() {        
        
        if (arquivos.length == 0) {
            if (status != null) {
                status.done(null);
            }
            return;
        }        
        
        try {
            // busca dimensões da primeira fatia, as demais devem ter as mesmas dimensões
            BufferedImage a = ImageIO.read(arquivos[0]);
            int w = a.getWidth();
            int h = a.getHeight();

            // buffer para os pixels
            final int data[] = new int[w * h];

            // prepara map para busca por valor do pixel
            final Map<Integer, Volume> map = new HashMap<>(volumes.length);
            for (Volume v: volumes) {
                map.put(v.getClasse().getCor().getRGB(), v);
            }

            int processado = 0;

            // varre as fatias calculando a quantidade de voxels de cada classe
            for (File fatia: arquivos) {
                carregaFatia(fatia, data, w, h);

                for (int p: data) {
                    Volume v = map.get(p);
                    if (v != null) {
                        v.voxels++;
                    }
                }

                processado++;
                if (status != null) {
                    status.status(processado);
                }                
            }

            // calcula volume com base no total de voxels e na resolução dos mesmos
            final double volumeVoxel = resolucaoVoxel[0] * resolucaoVoxel[1] * resolucaoVoxel[2];
            for (Volume v: volumes) {
                v.volume = v.voxels * volumeVoxel / (1000 * 1000 * 1000);
            }

            if (status != null) {
                status.done(null);
            }

        } catch (IOException ex) {
            if (status != null) {
                status.done(ex);
            }
        }

    }

    private static void carregaFatia(File file, int[] data, int w, int h) throws IOException {

        BufferedImage a = ImageIO.read(file);

        if (a.getWidth() != w || a.getHeight() != h) {
            throw new IOException("Arquivo com resolução diferente - " + file);
        }

        a.getRGB(0, 0, w, h, data, 0, w);

    }

    public Volume[] getVolumes() {
        return volumes;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
