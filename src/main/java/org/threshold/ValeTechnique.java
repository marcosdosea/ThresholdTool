package org.threshold;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;

import org.designroleminer.ClassMetricResult;
import org.designroleminer.LimiarMetrica;
import org.designroleminer.MethodMetricResult;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.persistence.csv.CSVFile;

public class ValeTechnique extends AbstractTechnique {

	/**
	 * Generate sheet with design role assigned to each class
	 * 
	 * @param classes
	 * @param fileResultado
	 */
	@Override
	public void generate(Collection<ClassMetricResult> classes, String fileResultado) {
		PersistenceMechanism pm = new CSVFile(fileResultado);
		pm.write("DesignRole;LOC;CC;Efferent;NOP;CLOC;");

		HashMap<String, Long> metodosPorDesignRole = new HashMap<String, Long>();
		long totalMetodos = obterTotalMetodosPorDesignRole(classes, metodosPorDesignRole);

		// METHOD THRESHOLD
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaLOC = new HashMap<String, HashMap<Integer, BigDecimal>>();
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaCC = new HashMap<String, HashMap<Integer, BigDecimal>>();
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaEfferent = new HashMap<String, HashMap<Integer, BigDecimal>>();
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaNOP = new HashMap<String, HashMap<Integer, BigDecimal>>();
		// CLASS THRESHOLD
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaCLOC = new HashMap<String, HashMap<Integer, BigDecimal>>();
		
		
		for (ClassMetricResult classe : classes) {
			for (MethodMetricResult method : classe.getMetricsByMethod().values()) {
				agrupaPorValorMetrica(distribuicaoCodigoPorMetricaLOC, method.getLinesOfCode(), 1,
						LimiarMetrica.METRICA_LOC + LimiarMetrica.DESIGN_ROLE_UNDEFINED);
				agrupaPorValorMetrica(distribuicaoCodigoPorMetricaCC, method.getComplexity(), 1,
						LimiarMetrica.METRICA_CC + LimiarMetrica.DESIGN_ROLE_UNDEFINED);
				agrupaPorValorMetrica(distribuicaoCodigoPorMetricaEfferent, method.getEfferentCoupling(), 1,
						LimiarMetrica.METRICA_EC + LimiarMetrica.DESIGN_ROLE_UNDEFINED);
				agrupaPorValorMetrica(distribuicaoCodigoPorMetricaNOP, method.getNumberOfParameters(), 1,
						LimiarMetrica.METRICA_NOP + LimiarMetrica.DESIGN_ROLE_UNDEFINED);
			}
			agrupaPorValorMetrica(distribuicaoCodigoPorMetricaCLOC, classe.getCLoc(), 1,
					LimiarMetrica.METRICA_CLOC + LimiarMetrica.DESIGN_ROLE_UNDEFINED);
		}

		// METHOD THRESHOLDS
		LimiarMetrica limiarLOC = obterLimiaresMetrica(distribuicaoCodigoPorMetricaLOC, totalMetodos, 3, 90, 95,
				LimiarMetrica.DESIGN_ROLE_UNDEFINED, LimiarMetrica.METRICA_LOC);
		LimiarMetrica limiarCC = obterLimiaresMetrica(distribuicaoCodigoPorMetricaCC, totalMetodos, 3, 90, 95,
				LimiarMetrica.DESIGN_ROLE_UNDEFINED, LimiarMetrica.METRICA_CC);
		LimiarMetrica limiarEfferent = obterLimiaresMetrica(distribuicaoCodigoPorMetricaEfferent, totalMetodos, 3, 90,
				95, LimiarMetrica.DESIGN_ROLE_UNDEFINED, LimiarMetrica.METRICA_EC);
		LimiarMetrica limiarNOP = obterLimiaresMetrica(distribuicaoCodigoPorMetricaNOP, totalMetodos, 3, 90, 95,
				LimiarMetrica.DESIGN_ROLE_UNDEFINED, LimiarMetrica.METRICA_NOP);

		// CLASS THRESHOLDS
		LimiarMetrica limiarCLOC = obterLimiaresMetrica(distribuicaoCodigoPorMetricaCLOC, totalMetodos, 3, 90, 95,
				LimiarMetrica.DESIGN_ROLE_UNDEFINED, LimiarMetrica.METRICA_CLOC);
		
		pm.write(LimiarMetrica.DESIGN_ROLE_UNDEFINED + ";" + limiarLOC.getLimiarMaximo() + ";"
				+ limiarCC.getLimiarMaximo() + ";" + limiarEfferent.getLimiarMaximo() + ";"
				+ limiarNOP.getLimiarMaximo() + ";"+ limiarCLOC.getLimiarMaximo() + ";");
	}

	private long obterTotalMetodosPorDesignRole(Collection<ClassMetricResult> classes,
			HashMap<String, Long> metodosPorDesignRole) {
		long total = 0;
		if (metodosPorDesignRole == null)
			metodosPorDesignRole = new HashMap<String, Long>();

		for (ClassMetricResult classe : classes) {
			int numeroMetodosClasse = classe.getMetricsByMethod().size();
			total += numeroMetodosClasse;
			Long somaMetodosPorDesignRole = metodosPorDesignRole.get(classe.getDesignRole());
			if (somaMetodosPorDesignRole == null) {
				metodosPorDesignRole.put(classe.getDesignRole(), Long.valueOf(numeroMetodosClasse));
			} else {
				somaMetodosPorDesignRole += numeroMetodosClasse;
				metodosPorDesignRole.put(classe.getDesignRole(), somaMetodosPorDesignRole);
			}
		}
		return total;
	}

}
