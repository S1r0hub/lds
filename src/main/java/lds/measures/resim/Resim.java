/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lds.measures.resim;

import lds.config.Config;
import lds.resource.R;
import org.openrdf.model.URI;

/**
 * This class is part of the Resource Similarity measure.<br/>
 * It implements normalized distance measures.
 * 
 * @author Fouad Komeiha
 */
public class Resim extends ResourceSimilarity {

	public Resim(Config config) throws Exception {
		super(config);
	}

	@Override
	public double LDSD(R a, R b) {

		double cdA_norm = 0, cdB_norm = 0, cii_norm = 0, cio_norm = 0;

		for (URI l : edges) {
			cdA_norm = cdA_norm + Cd_normalized(l, a, b);
			cdB_norm = cdB_norm + Cd_normalized(l, b, a);
			cii_norm = cii_norm + Cii_normalized(l, a, b);
			cio_norm = cio_norm + Cio_normalized(l, a, b);
		}

		return 1 / (1 + cdA_norm + cdB_norm + cii_norm + cio_norm);
	}

	@Override
	public double Cd_normalized(URI l, R a, R b) {
		
		int cd = Cd(l, a, b), cd_l = 0;
		double cd_norm = 0;

		if (cd != 0) {
			
			cd_l = Cd(l, a);
			cd_norm = (double) cd; // final cd result
			
			// normalize if possible (prevent log10(0)), otherwise assume cd / 1
			if (cd_l != 0) {
				cd_norm /= 1 + Math.log10(cd_l);
			}
		}

		return cd_norm;
	}

	@Override
	public double Cii_normalized(URI l, R a, R b) {

		int ciiA, ciiB, cii;
		double cii_norm = 0;

		cii = Cii(l, a, b);

		if (cii != 0) {

			ciiA = Cii(l, a);
			ciiB = Cii(l, b);
			cii_norm = (double) cii; // final cii result
			
			// normalize if possible (prevent log10(0)), otherwise assume cd / 1
			if ((ciiA + ciiB) != 0) {
				cii_norm /= 1 + Math.log10((ciiA + ciiB) / 2);
			}
		}

		return cii_norm;
	}

	@Override
	public double Cio_normalized(URI l, R a, R b) {
		
		int cioA, cioB, cio;
		double cio_norm = 0;

		cio = Cio(l, a, b);

		if (cio != 0) {
			
			cioA = Cio(l, a);
			cioB = Cio(l, b);
			cio_norm = (double) cio; // final cio result

			// normalize if possible (prevent log10(0)), otherwise assume cd / 1
			if ((cioA + cioB) != 0) {
				cio_norm /= 1 + Math.log10((cioA + cioB) / 2);
			}
		}

		return cio_norm;
	}

	@Override
	public int Cii(URI li, URI lj, R a, R b) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int Cii(URI li, URI lj, R k) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public double Cii_normalized(URI li, URI lj, R a, R b) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int Cio(URI li, URI lj, R a, R b) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int Cio(URI li, URI lj, R k) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public double Cio_normalized(URI li, URI lj, R a, R b) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
