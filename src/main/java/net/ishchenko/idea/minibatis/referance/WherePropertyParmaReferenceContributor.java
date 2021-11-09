package net.ishchenko.idea.minibatis.referance;


import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.patterns.PsiElementPattern;
import com.intellij.patterns.XmlPatterns;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiExpressionList;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceExpression;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.ProcessingContext;
import com.intellij.util.Query;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import net.ishchenko.idea.minibatis.constant.IbatisConstant;
import net.ishchenko.idea.minibatis.model.sqlmap.GroupTwo;
import net.ishchenko.idea.minibatis.model.sqlmap.Sql;
import net.ishchenko.idea.minibatis.util.JavaUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * 1
 * @author jinwenbiao
 * @since 2021/11/9 15:51
 */
public class WherePropertyParmaReferenceContributor extends PsiReferenceContributor {
	public static final PsiElementPattern.Capture<PsiElement> EL_VAR_COMMENT = PlatformPatterns.psiElement(PsiElement.class)
			.inside(XmlPatterns.xmlTag().withName(IbatisConstant.ARR)).inside(XmlPatterns.xmlAttribute().withName(IbatisConstant.PROPERTY));
	@Override
	public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
		registrar.registerReferenceProvider(EL_VAR_COMMENT, new PsiReferenceProvider() {
			@Override
			public PsiReference @NotNull [] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext processingContext) {
				if (!(element instanceof XmlAttributeValue)) {
					return PsiReference.EMPTY_ARRAY;
				}
				String text = ((XmlAttributeValue) element).getValue();
				if (StringUtils.isEmpty(text)) {
					return PsiReference.EMPTY_ARRAY;
				}
				Project project = element.getProject();
				DomElement domElement = DomUtil.getDomElement(element);
				GroupTwo groupTwo = DomUtil.getParentOfType(domElement, GroupTwo.class, true); // select insert update delete
				if (groupTwo == null) {
					Sql sql = DomUtil.getParentOfType(domElement, Sql.class, true);
					if (sql == null || sql.getId() == null || sql.getId().getXmlAttributeValue() == null) {
						return PsiReference.EMPTY_ARRAY;
					}
					Query<PsiReference> psiReferences = ReferencesSearch.search(sql.getId().getXmlAttributeValue(), GlobalSearchScope.projectScope(project));
					Collection<PsiReference> psiReferenceCollection = psiReferences.findAll();
					for (PsiReference psiReference : psiReferenceCollection) { // 引用sql的地方
						PsiElement psiElement = psiReference.getElement();
						DomElement includeDomElement = DomUtil.getDomElement(psiElement);
						GroupTwo groupTwo2 = DomUtil.getParentOfType(includeDomElement, GroupTwo.class, true);
						if (groupTwo2 != null) {
							groupTwo = groupTwo2;
							break;
						}
					}
				}
				if (groupTwo == null) {
					return PsiReference.EMPTY_ARRAY;
				}
				return new PsiReference[]{new HashMarkReference(element, groupTwo, text)};

			}
		});
	}
	private static class HashMarkReference extends PsiReferenceBase<PsiElement> {

		private final String value;
		private final GroupTwo groupTwo;

		public HashMarkReference(@NotNull PsiElement element, GroupTwo groupTwo, String value) {
			super(element);
			this.value = value;
			this.groupTwo = groupTwo;
		}

		@Override
		public @Nullable PsiElement resolve() {
			if (groupTwo == null) {
				return null;
			}
			return m1(myElement.getProject(), groupTwo, value);

		}

		private PsiField m1(Project project, GroupTwo groupTwo, String value) {
			XmlAttributeValue xmlAttributeValue = groupTwo.getId().getXmlAttributeValue();
			if (xmlAttributeValue == null) {
				return null;
			}
			Query<PsiReference> psiReferences = ReferencesSearch.search(xmlAttributeValue, GlobalSearchScope.projectScope(project));
			Collection<PsiReference> methodBody = psiReferences.findAll();
			for (PsiReference psiReference : methodBody) {
				PsiElement element = psiReference.getElement();
				PsiExpressionList psiExpressionList = PsiTreeUtil.getParentOfType(element, PsiExpressionList.class, true);
				if (psiExpressionList == null) {
					return null;
				}
				PsiExpression[] expressions = psiExpressionList.getExpressions();
				if (ArrayUtils.isEmpty(expressions)) {
					return null;
				}
				for (PsiExpression expression : expressions) {
					if (expression instanceof PsiReferenceExpression) {
						PsiType type = expression.getType();
						if (type instanceof PsiClassReferenceType) {
							String className = type.getCanonicalText();
							PsiClass clazz = JavaUtils.findClazz(project, className);
							if (clazz == null) {
								return null;
							}
							PsiField[] allFields = clazz.getAllFields();
							for (PsiField psiField : allFields) {
								if (IbatisConstant.SERIAL_VERSION_UID.equals(psiField.getName())) {
									continue;
								}
								if (StringUtils.equals(value, psiField.getName())) {
									return psiField;
								}
							}
						}
					}
				}
			}
			return null;
		}
	}
}
