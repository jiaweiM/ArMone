package cn.ac.dicp.gp1809.glyco.oglycan;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static org.testng.Assert.*;

/**
 * @author JiaweiMao
 * @version 1.0.0
 * @since 28 Jun 2018, 1:29 PM
 */
public class OGlycanUnitStatisticTest
{

    @Test
    public void testGetUnitIdMap()
    {
        OGlycanUnit[] units = new OGlycanUnit[]{OGlycanUnit.core1_1, OGlycanUnit.core1_2, OGlycanUnit.core1_3,
                OGlycanUnit.core1_4, OGlycanUnit.core1_5, OGlycanUnit.core2_1,
                OGlycanUnit.core2_2, OGlycanUnit.core2_3, OGlycanUnit.core2_4, OGlycanUnit.core2_5, OGlycanUnit.core2_8, OGlycanUnit.core2_9};
        OGlycanUnitStatistic stat = new OGlycanUnitStatistic(units);

        Map<Integer, OGlycanUnit> oGlycanUnitMap = new HashMap<>();
        for (OGlycanUnit unit : OGlycanUnit.values()) {
            oGlycanUnitMap.put(unit.getID(), unit);
        }

        HashMap<Double, int[][]> unitIdMap = stat.getUnitIdMap();
        System.out.println(unitIdMap.size());
        for (Double mz : unitIdMap.keySet()) {
            int[][] ints = unitIdMap.get(mz);
            StringJoiner joiner = new StringJoiner(":");
            for (int[] ints1 : ints) {
                StringJoiner joiner1 = new StringJoiner(",");
                for (int valeu : ints1) {
                    OGlycanUnit unit = oGlycanUnitMap.get(valeu);
                    joiner1.add(unit.getComposition());
                }
                joiner.add(joiner1.toString());
            }
            System.out.println(mz + ";" + joiner.toString());
        }

//        double[] mzList = stat.getMzList();
//        for(double mz : mzList){
//            System.out.println(mz);
//        }
    }
}