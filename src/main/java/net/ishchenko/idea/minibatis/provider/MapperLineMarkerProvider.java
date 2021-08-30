package net.ishchenko.idea.minibatis.provider;


import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.CommonProcessors;
import com.intellij.util.xml.DomElement;
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import net.ishchenko.idea.minibatis.service.JavaService;
import net.ishchenko.idea.minibatis.util.Icons;
import net.ishchenko.idea.minibatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * <h2></h2>
 * description:
 * @author wenbiao.jin
 * @version 1.0
 * @since 2021/8/28 12:05
 */
public class MapperLineMarkerProvider extends RelatedItemLineMarkerProvider {
	private static final Function<DomElement, XmlTag> FUN = new Function<DomElement, XmlTag>() {
		@Override
		public XmlTag apply(DomElement domElement) {
			return domElement.getXmlTag();
		}
	};

	@Override
	protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
		if (element instanceof PsiNameIdentifierOwner && JavaUtils.isElementWithinInterface(element)) {
			CommonProcessors.CollectProcessor<IdDomElement> processor = new CommonProcessors.CollectProcessor<IdDomElement>();
			JavaService.getInstance(element.getProject()).process(element, processor);
			Collection<IdDomElement> results = processor.getResults();
			if (!results.isEmpty()) {
				NavigationGutterIconBuilder<PsiElement> builder =
						NavigationGutterIconBuilder.create(Icons.MAPPER_LINE_MARKER_ICON)
								.setAlignment(GutterIconRenderer.Alignment.CENTER)
								.setTargets(Collections2.transform(results, FUN))
								.setTooltipTitle("Navigation to target in mapper xml");
				result.add(builder.createLineMarkerInfo(((PsiNameIdentifierOwner) element).getNameIdentifier()));
			}
		}
	}
}
