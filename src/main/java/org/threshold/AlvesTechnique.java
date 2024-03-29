package org.threshold;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;

import org.designroleminer.ClassMetricResult;
import org.designroleminer.LimiarMetrica;
import org.designroleminer.MethodMetricResult;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.persistence.csv.CSVFile;

public class AlvesTechnique extends AbstractTechnique {

	/**
	 * Generate sheet with design role assigned to each class
	 * 
	 * @param classes
	 * @param fileResultado
	 */
	@Override
	public void generate(Collection<ClassMetricResult> classes, String fileResultado) {
		PersistenceMechanism pm = new CSVFile(fileResultado);
		pm.write("DesignRole;LOC;CC;Efferent;NOP;CLOC");

		HashMap<String, Long> linhasDeCodigoPorDesignRole = new HashMap<String, Long>();
		long totalLoc = obterTotalLinhasCodigoPorDesignRole(classes, linhasDeCodigoPorDesignRole);

		// METHOD THRESHOLDS
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaLOC = new HashMap<String, HashMap<Integer, BigDecimal>>();
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaCC = new HashMap<String, HashMap<Integer, BigDecimal>>();
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaEfferent = new HashMap<String, HashMap<Integer, BigDecimal>>();
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaNOP = new HashMap<String, HashMap<Integer, BigDecimal>>();
		// CLASS THRESHOLDS
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaCLOC = new HashMap<String, HashMap<Integer, BigDecimal>>();

		for (ClassMetricResult classe : classes) {
			for (MethodMetricResult method : classe.getMetricsByMethod().values()) {
				agrupaPorValorMetrica(distribuicaoCodigoPorMetricaLOC, method.getLinesOfCode(), method.getLinesOfCode(),
						LimiarMetrica.METRICA_LOC + LimiarMetrica.DESIGN_ROLE_UNDEFINED);
				agrupaPorValorMetrica(distribuicaoCodigoPorMetricaCC, method.getComplexity(), method.getLinesOfCode(),
						LimiarMetrica.METRICA_CC + LimiarMetrica.DESIGN_ROLE_UNDEFINED);
				agrupaPorValorMetrica(distribuicaoCodigoPorMetricaEfferent, method.getEfferentCoupling(),
						method.getLinesOfCode(), LimiarMetrica.METRICA_EC + LimiarMetrica.DESIGN_ROLE_UNDEFINED);
				agrupaPorValorMetrica(distribuicaoCodigoPorMetricaNOP, method.getNumberOfParameters(),
						method.getLinesOfCode(), LimiarMetrica.METRICA_NOP + LimiarMetrica.DESIGN_ROLE_UNDEFINED);
			}
			agrupaPorValorMetrica(distribuicaoCodigoPorMetricaCLOC, classe.getCLoc(), classe.getCLoc(),
					LimiarMetrica.METRICA_CLOC + LimiarMetrica.DESIGN_ROLE_UNDEFINED);

		}

		// method thresholds
		LimiarMetrica limiarLOC = obterLimiaresMetrica(distribuicaoCodigoPorMetricaLOC, totalLoc, 5, 70, 90,
				LimiarMetrica.DESIGN_ROLE_UNDEFINED, LimiarMetrica.METRICA_LOC);
		LimiarMetrica limiarCC = obterLimiaresMetrica(distribuicaoCodigoPorMetricaCC, totalLoc, 5, 70, 90,
				LimiarMetrica.DESIGN_ROLE_UNDEFINED, LimiarMetrica.METRICA_CC);
		LimiarMetrica limiarEfferent = obterLimiaresMetrica(distribuicaoCodigoPorMetricaEfferent, totalLoc, 5, 70, 90,
				LimiarMetrica.DESIGN_ROLE_UNDEFINED, LimiarMetrica.METRICA_EC);
		LimiarMetrica limiarNOP = obterLimiaresMetrica(distribuicaoCodigoPorMetricaNOP, totalLoc, 5, 90, 95,
				LimiarMetrica.DESIGN_ROLE_UNDEFINED, LimiarMetrica.METRICA_NOP);

		// class thresholds
		LimiarMetrica limiarCLOC = obterLimiaresMetrica(distribuicaoCodigoPorMetricaCLOC, totalLoc, 5, 70, 90,
				LimiarMetrica.DESIGN_ROLE_UNDEFINED, LimiarMetrica.METRICA_CLOC);

		pm.write(LimiarMetrica.DESIGN_ROLE_UNDEFINED + ";" + limiarLOC.getLimiarMaximo() + ";"
				+ limiarCC.getLimiarMaximo() + ";" + limiarEfferent.getLimiarMaximo() + ";"
				+ limiarNOP.getLimiarMaximo() + ";" + limiarCLOC.getLimiarMaximo() + ";");
	}
}
