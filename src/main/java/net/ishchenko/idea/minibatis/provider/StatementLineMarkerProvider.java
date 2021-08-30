package net.ishchenko.idea.minibatis.provider;


import com.google.common.collect.ImmutableList;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Function;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import net.ishchenko.idea.minibatis.model.sqlmap.Delete;
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import net.ishchenko.idea.minibatis.model.sqlmap.Insert;
import net.ishchenko.idea.minibatis.model.sqlmap.Select;
import net.ishchenko.idea.minibatis.model.sqlmap.Update;
import net.ishchenko.idea.minibatis.util.Icons;
import net.ishchenko.idea.minibatis.util.JavaUtils;
import net.ishchenko.idea.minibatis.util.SqlMapperUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;

/**
 * <h2></h2>
 * description:
 * @author wenbiao.jin
 * @version 1.0
 * @since 2021/8/28 12:32
 */
public class StatementLineMarkerProvider<F extends PsiElement, T> implements LineMarkerProvider {
	private static final ImmutableList<Class<? extends IdDomElement>> TARGET_TYPES = ImmutableList.of(
			Select.class, Update.class, Insert.class, Delete.class
	);

	@Override
	public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement psiElement) {
		if (!isTheElement(psiElement)) {
			return null;
		}
		PsiMethod apply = apply((XmlTag) psiElement);
		if (apply == null) {
			return null;
		}
		return new LineMarkerInfo(
				psiElement,
				psiElement.getTextRange(),
				Icons.STATEMENT_LINE_MARKER_ICON,
				Pass.UPDATE_ALL,
				getTooltipProvider(apply),
				getNavigationHandler(apply),
				GutterIconRenderer.Alignment.CENTER);
	}

	@Override
	public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {

	}

	private Function<F, String> getTooltipProvider(final PsiMethod target) {
		return new Function<F, String>() {
			@Override
			public String fun(F from) {
				PsiMethod psiMethod = (PsiMethod) target;
				return "Data access object found - " + psiMethod.getContainingClass().getQualifiedName();
			}

		};
	}

	private GutterIconNavigationHandler<F> getNavigationHandler(final PsiMethod target) {
		return new GutterIconNavigationHandler<F>() {
			@Override
			public void navigate(MouseEvent e, F from) {
				Navigatable navigationElement = (Navigatable) target.getNavigationElement();
				navigationElement.navigate(true);
			}
		};
	}

	public PsiMethod apply(@NotNull XmlTag from) {
		DomElement domElement = DomUtil.getDomElement(from);
		return null == domElement ? null : JavaUtils.findMethod(from.getProject(), (IdDomElement) domElement);
	}

	public boolean isTheElement(@NotNull PsiElement element) {
		return element instanceof XmlTag
				&& SqlMapperUtils.isElementWithinMybatisFile(element)
				&& isTargetType(element);
	}

	private boolean isTargetType(PsiElement element) {
		DomElement domElement = DomUtil.getDomElement(element);
		for (Class<?> clazz : TARGET_TYPES) {
			if (clazz.isInstance(domElement))
				return true;
		}
		return false;
	}

}
