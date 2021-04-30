package net.ishchenko.idea.minibatis.converter;


import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.Query;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.PsiClassConverter;
import com.intellij.util.xml.ResolvingConverter;
import net.ishchenko.idea.minibatis.model.sqlmap.GroupTwo;
import net.ishchenko.idea.minibatis.model.sqlmap.Sql;
import net.ishchenko.idea.minibatis.referance.SelectReference;
import net.ishchenko.idea.minibatis.util.IbatisConstant;
import net.ishchenko.idea.minibatis.util.SqlMapperUtils;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author yanglin
 */
public class PropertyConverter extends ResolvingConverter<PsiClass> implements CustomReferenceConverter<PsiClass> {

    private static final Logger log = LoggerFactory.getLogger(PropertyConverter.class);

    private final PsiClassConverter delegate = new PsiClassConverter();

    @Nullable
    @Override
    public PsiClass fromString(@Nullable @NonNls String s, ConvertContext context) {
        if (StringUtil.isEmptyOrSpaces(s)) {
            return null;
        }
        return null;
    }

    @Nullable
    @Override
    public String toString(@Nullable PsiClass psiClass, ConvertContext context) {
        return delegate.toString(psiClass, context);
    }

    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue<PsiClass> value, PsiElement element, ConvertContext context) {
        if (((XmlAttributeValue) element).getValue().contains(IbatisConstant.DOT_SEPARATOR)) {
            return delegate.createReferences(value, element, context);
        } else {
            DomElement invocationElement = context.getInvocationElement();
            GroupTwo groupTwo = DomUtil.getParentOfType(invocationElement, GroupTwo.class, true);
            if (groupTwo != null) {
                return new SelectReference(value.getStringValue(), element, ElementManipulators.getOffsetInElement(element), groupTwo).getPsiReferences();
            } else {
                Sql sql = DomUtil.getParentOfType(invocationElement, Sql.class, true);
                if (sql == null || sql.getId() == null || sql.getId().getXmlAttributeValue() == null) {
                    return PsiReference.EMPTY_ARRAY;
                }
                XmlAttributeValue xmlAttributeValue = sql.getId().getXmlAttributeValue();
                Query<PsiReference> search = ReferencesSearch.search(xmlAttributeValue, GlobalSearchScope.fileScope(element.getContainingFile()));
                List<PsiReference> allReference = (List<PsiReference>) search.findAll();
                if (CollectionUtils.isEmpty(allReference)) {
                    return PsiReference.EMPTY_ARRAY;
                }
                PsiElement sqlReference = allReference.get(0).getElement();
                GroupTwo parentIdDomElement = (GroupTwo) SqlMapperUtils.findParentIdDomElement(sqlReference);
                return new SelectReference(value.getStringValue(), element, ElementManipulators.getOffsetInElement(element), parentIdDomElement).getPsiReferences();
            }
        }
    }

    @Override
    public @NotNull Collection<? extends PsiClass> getVariants(ConvertContext convertContext) {
        return Collections.emptyList();
    }

}
