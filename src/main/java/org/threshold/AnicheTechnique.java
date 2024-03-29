package org.threshold;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;

import org.designroleminer.ClassMetricResult;
import org.designroleminer.LimiarMetrica;
import org.designroleminer.MethodMetricResult;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.persistence.csv.CSVFile;

public class AnicheTechnique extends AbstractTechnique {

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

		HashMap<String, Long> linhasDeCodigoPorArchitecturalRole = new HashMap<String, Long>();
		obterTotalLinhasCodigoPorArchitecturalRole(classes, linhasDeCodigoPorArchitecturalRole);

		// METHOD THRESHOLDS
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaLOC = new HashMap<String, HashMap<Integer, BigDecimal>>();
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaCC = new HashMap<String, HashMap<Integer, BigDecimal>>();
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaEfferent = new HashMap<String, HashMap<Integer, BigDecimal>>();
		// CLASS THRESHOLDS
		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaNOP = new HashMap<String, HashMap<Integer, BigDecimal>>();

		HashMap<String, HashMap<Integer, BigDecimal>> distribuicaoCodigoPorMetricaCLOC = new HashMap<String, HashMap<Integer, BigDecimal>>();

		for (ClassMetricResult classe : classes) {
			String architecturalRole = LimiarMetrica.DESIGN_ROLE_UNDEFINED;
			if (classe.isArchitecturalRole())
				architecturalRole = classe.getDesignRole();

			for (MethodMetricResult method : classe.getMetricsByMethod().values()) {
				agrupaPorValorMetrica(distribuicaoCodigoPorMetricaLOC, method.getLinesOfCode(), method.getLinesOfCode(),
						LimiarMetrica.METRICA_LOC + architecturalRole);
				agrupaPorValorMetrica(distribuicaoCodigoPorMetricaCC, method.getComplexity(), method.getLinesOfCode(),
						LimiarMetrica.METRICA_CC + architecturalRole);
				agrupaPorValorMetrica(distribuicaoCodigoPorMetricaEfferent, method.getEfferentCoupling(),
						method.getLinesOfCode(), LimiarMetrica.METRICA_EC + architecturalRole);
				agrupaPorValorMetrica(distribuicaoCodigoPorMetricaNOP, method.getNumberOfParameters(),
						method.getLinesOfCode(), LimiarMetrica.METRICA_NOP + architecturalRole);
			}
			agrupaPorValorMetrica(distribuicaoCodigoPorMetricaCLOC, classe.getCLoc(),
					classe.getCLoc(), LimiarMetrica.METRICA_CLOC + architecturalRole);
			
		}

		for (String architecuturalRole : linhasDeCodigoPorArchitecturalRole.keySet()) {
			architecuturalRole = architecuturalRole.toUpperCase();
			long totalLOCPorArchitecturalRole = linhasDeCodigoPorArchitecturalRole.get(architecuturalRole);
			// METHOD THRESHOLDS
			LimiarMetrica limiarLOC = obterLimiaresMetrica(distribuicaoCodigoPorMetricaLOC,
					totalLOCPorArchitecturalRole, 5, 70, 90, architecuturalRole, LimiarMetrica.METRICA_LOC);
			LimiarMetrica limiarCC = obterLimiaresMetrica(distribuicaoCodigoPorMetricaCC, totalLOCPorArchitecturalRole,
					5, 70, 90, architecuturalRole, LimiarMetrica.METRICA_CC);
			LimiarMetrica limiarEfferent = obterLimiaresMetrica(distribuicaoCodigoPorMetricaEfferent,
					totalLOCPorArchitecturalRole, 5, 70, 90, architecuturalRole, LimiarMetrica.METRICA_EC);
			LimiarMetrica limiarNOP = obterLimiaresMetrica(distribuicaoCodigoPorMetricaNOP,
					totalLOCPorArchitecturalRole, 5, 90, 95, architecuturalRole, LimiarMetrica.METRICA_NOP);
			// CLASS THRESHOLDS
			LimiarMetrica limiarCLOC = obterLimiaresMetrica(distribuicaoCodigoPorMetricaCLOC,
					totalLOCPorArchitecturalRole, 5, 70, 90, architecuturalRole, LimiarMetrica.METRICA_CLOC);
			pm.write(architecuturalRole + ";" + limiarLOC.getLimiarMaximo() + ";" + limiarCC.getLimiarMaximo() + ";"
					+ limiarEfferent.getLimiarMaximo() + ";" + limiarNOP.getLimiarMaximo() + ";" + limiarCLOC.getLimiarMaximo() + ";");
		}
	}

	private long obterTotalLinhasCodigoPorArchitecturalRole(Collection<ClassMetricResult> classes,
			HashMap<String, Long> linhasDeCodigoPorDesignRole) {
		long total = 0;
		if (linhasDeCodigoPorDesignRole == null)
			linhasDeCodigoPorDesignRole = new HashMap<String, Long>();

		for (ClassMetricResult classe : classes) {
			String architecturalRole = LimiarMetrica.DESIGN_ROLE_UNDEFINED;
			if (classe.isArchitecturalRole())
				architecturalRole = classe.getDesignRole();

			for (MethodMetricResult method : classe.getMetricsByMethod().values()) {
				total += method.getLinesOfCode();
				Long somaLocPorDesignRole = linhasDeCodigoPorDesignRole.get(architecturalRole);
				if (somaLocPorDesignRole == null) {
					linhasDeCodigoPorDesignRole.put(architecturalRole, Long.valueOf(method.getLinesOfCode()));
				} else {
					somaLocPorDesignRole += method.getLinesOfCode();
					linhasDeCodigoPorDesignRole.put(architecturalRole, somaLocPorDesignRole);
				}
			}
		}
		return total;
	}

}
