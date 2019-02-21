package cn.ac.dicp.gp1809.glyco.oglycan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 27 Jun 2018, 7:26 PM
 */
public class OGlycanUnitStatistic
{
    private double[] mzs;
    private HashMap<Double, int[][]> unitmap;

    OGlycanUnitStatistic(OGlycanUnit[] units)
    {
        this.enumerate(units);
    }

    private void enumerate(OGlycanUnit[] units)
    {
        // composition -> ids
        HashMap<String, ArrayList<int[]>> map = new HashMap<>();
        for (OGlycanUnit unit : units) {
            int[] comp = unit.getCompCount();
            String key = comp[0] + "_" + comp[1] + "_" + comp[2] + "_" + comp[3] + "_" + comp[4];
            if (map.containsKey(key)) {
                map.get(key).add(new int[]{unit.getID()});
            } else {
                ArrayList<int[]> list = new ArrayList<>();
                list.add(new int[]{unit.getID()});
                map.put(key, list);
            }
        }

        // combinations 2
        for (int i1 = 0; i1 < units.length; i1++) {
            for (int i2 = i1; i2 < units.length; i2++) {
                int[] comp = new int[5];
                for (int j = 0; j < 5; j++) {
                    comp[j] += units[i1].getCompCount()[j];
                    comp[j] += units[i2].getCompCount()[j];
                }
                String key = comp[0] + "_" + comp[1] + "_" + comp[2] + "_" + comp[3] + "_" + comp[4];
                if (map.containsKey(key)) {
                    map.get(key).add(new int[]{units[i1].getID(), units[i2].getID()});
                } else {
                    ArrayList<int[]> list = new ArrayList<>();
                    list.add(new int[]{units[i1].getID(), units[i2].getID()});
                    map.put(key, list);
                }
            }
        }

        // combination 3
        for (int i1 = 0; i1 < units.length; i1++) {
            for (int i2 = i1; i2 < units.length; i2++) {
                for (int i3 = i2; i3 < units.length; i3++) {
                    int[] comp = new int[5];
                    for (int j = 0; j < 5; j++) {
                        comp[j] += units[i1].getCompCount()[j];
                        comp[j] += units[i2].getCompCount()[j];
                        comp[j] += units[i3].getCompCount()[j];
                    }
                    String key = comp[0] + "_" + comp[1] + "_" + comp[2] + "_" + comp[3] + "_" + comp[4];
                    if (map.containsKey(key)) {
                        map.get(key).add(new int[]{units[i1].getID(), units[i2].getID(), units[i3].getID()});
                    } else {
                        ArrayList<int[]> list = new ArrayList<>();
                        list.add(new int[]{units[i1].getID(), units[i2].getID(), units[i3].getID()});
                        map.put(key, list);
                    }
                }
            }
        }

        // combination 4
        for (int i1 = 0; i1 < units.length; i1++) {
            for (int i2 = i1; i2 < units.length; i2++) {
                for (int i3 = i2; i3 < units.length; i3++) {
                    for (int i4 = i3; i4 < units.length; i4++) {
                        int[] comp = new int[5];
                        for (int j = 0; j < 5; j++) {
                            comp[j] += units[i1].getCompCount()[j];
                            comp[j] += units[i2].getCompCount()[j];
                            comp[j] += units[i3].getCompCount()[j];
                            comp[j] += units[i4].getCompCount()[j];
                        }
                        String key = comp[0] + "_" + comp[1] + "_" + comp[2] + "_" + comp[3] + "_" + comp[4];
                        if (map.containsKey(key)) {
                            map.get(key).add(new int[]{units[i1].getID(), units[i2].getID(), units[i3].getID(), units[i4].getID()});
                        }
                    }
                }
            }
        }

        // combination 5
        for (int i1 = 0; i1 < units.length; i1++) {
            for (int i2 = i1; i2 < units.length; i2++) {
                for (int i3 = i2; i3 < units.length; i3++) {
                    for (int i4 = i3; i4 < units.length; i4++) {
                        for (int i5 = i4; i5 < units.length; i5++) {
                            int[] comp = new int[5];
                            for (int j = 0; j < 5; j++) {
                                comp[j] += units[i1].getCompCount()[j];
                                comp[j] += units[i2].getCompCount()[j];
                                comp[j] += units[i3].getCompCount()[j];
                                comp[j] += units[i4].getCompCount()[j];
                                comp[j] += units[i5].getCompCount()[j];
                            }
                            String key = comp[0] + "_" + comp[1] + "_" + comp[2] + "_" + comp[3] + "_" + comp[4];
                            if (map.containsKey(key)) {
                                map.get(key).add(new int[]{units[i1].getID(), units[i2].getID(), units[i3].getID(),
                                        units[i4].getID(), units[i5].getID()});
                            }
                        }
                    }
                }
            }
        }

        this.mzs = new double[map.size()];
        this.unitmap = new HashMap<>();
        int id = 0;
        double[] monomass = new double[]{203.079373, 162.052825, 291.095416635, 146.057909, 81.05857};

        for (String key : map.keySet()) {
            String[] cs = key.split("_");
            this.mzs[id] = 0;
            for (int i = 0; i < cs.length; i++) {
                this.mzs[id] += monomass[i] * Double.parseDouble(cs[i]);
            }
            ArrayList<int[]> list = map.get(key);
            int[][] unitIds = list.toArray(new int[list.size()][]);
            this.unitmap.put(mzs[id], unitIds);

            id++;
        }

        Arrays.sort(this.mzs);
    }

    public double[] getMzList()
    {
        return this.mzs;
    }

    public HashMap<Double, int[][]> getUnitIdMap()
    {
        return this.unitmap;
    }

}
