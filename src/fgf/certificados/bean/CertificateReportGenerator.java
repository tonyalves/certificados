package fgf.certificados.bean;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import fgf.certificados.util.DateUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

public class CertificateReportGenerator {
	private Certificate certificate;

	public CertificateReportGenerator(Certificate certificate) {
		this.certificate = certificate;
	}

	public void generateReport() throws JRException, FileNotFoundException {

		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext()
				.getContext();
		String path = servletContext.getRealPath("/") + "resources/";
		String sourceFile = path + "certificate.jrxml";

		JasperCompileManager.compileReportToFile(sourceFile);

		List<Certificate> certificates = new ArrayList<>();

		certificates.add(certificate);
		Map<String, Object> parametros = new HashMap<String, Object>();
		//
		JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(certificates);

		JasperPrint print = JasperFillManager.fillReport(path + "certificate.jasper", parametros, beanColDataSource);

		JasperExportManager.exportReportToPdfFile(print,
				path + "certificate_" + certificate.getLecturer() + "_" + formattedCurrentDate() + ".pdf");
	}

	public String formattedCurrentDate() {
		DateUtil util = new DateUtil();
		return util.formattedDate(new Date(), "_");
	}
}