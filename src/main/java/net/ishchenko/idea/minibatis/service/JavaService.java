package net.ishchenko.idea.minibatis.service;


import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.util.Processor;
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import net.ishchenko.idea.minibatis.model.sqlmap.SqlMap;
import net.ishchenko.idea.minibatis.util.SqlMapperUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * <h2></h2>
 * description:
 * @author wenbiao.jin
 * @version 1.0
 * @since 2021/8/28 12:07
 */
public class JavaService {
	public static JavaService getInstance( Project project) {
		return ServiceManager.getService(project, JavaService.class);
	}

	public void process(@NotNull PsiElement target, @NotNull Processor processor) {
		if (target instanceof PsiMethod) {
			process((PsiMethod) target, processor);
		} else if (target instanceof PsiClass) {
			process((PsiClass) target, processor);
		}
	}

	public void process(@NotNull PsiMethod psiMethod, @NotNull Processor<IdDomElement> processor) {
		PsiClass psiClass = psiMethod.getContainingClass();
		if (null == psiClass) return;
		String id = psiClass.getQualifiedName() + "." + psiMethod.getName();
		Collection<SqlMap> mappers = SqlMapperUtils.findMappers(psiMethod.getProject());
		for (SqlMap mapper : mappers) {
			for (IdDomElement idDomElement : mapper.getDaoElements()) {
				if (StringUtils.equals(SqlMapperUtils.getIdSignature(idDomElement), id)) {
					processor.process(idDomElement);
				}
			}
		}
	}

	public void process(@NotNull PsiClass clazz, @NotNull Processor<SqlMap> processor) {
		String ns = clazz.getQualifiedName();
		Collection<SqlMap> mappers = SqlMapperUtils.findMappers(clazz.getProject());
		for (SqlMap mapper : mappers) {
			if (SqlMapperUtils.getNamespace(mapper).equals(ns)) {
				processor.process(mapper);
			}
		}
	}
}
