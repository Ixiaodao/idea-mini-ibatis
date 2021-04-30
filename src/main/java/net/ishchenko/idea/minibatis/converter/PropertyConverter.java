package net.ishchenko.idea.minibatis.converter;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
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
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import net.ishchenko.idea.minibatis.model.sqlmap.ParameterMap;
import net.ishchenko.idea.minibatis.model.sqlmap.Sql;
import net.ishchenko.idea.minibatis.model.sqlmap.SqlMap;
import net.ishchenko.idea.minibatis.referance.SelectReference;
import net.ishchenko.idea.minibatis.util.IbatisConstant;
import net.ishchenko.idea.minibatis.util.JavaUtils;
import net.ishchenko.idea.minibatis.util.SqlMapperUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yanglin
 */
public class PropertyConverter extends ResolvingConverter<PsiField> implements CustomReferenceConverter<PsiField> {

    @Override
    public PsiReference @NotNull [] createReferences(GenericDomValue<PsiField> value, PsiElement element, ConvertContext context) {
        return PsiClassConverter.createJavaClassReferenceProvider(value, null, new ValueReferenceProvider(context)).getReferencesByElement(element);

    }

    private class ValueReferenceProvider extends JavaClassReferenceProvider {

        private final ConvertContext context;

        private ValueReferenceProvider(ConvertContext context) {
            this.context = context;
        }

        @Nullable
        @Override
        public GlobalSearchScope getScope(@NotNull Project project) {
            return GlobalSearchScope.allScope(project);
        }

        /**
         * It looks like hacking here, as it's a little hard to handle so many different cases as JetBrains does
         */
        @Override
        public PsiReference @NotNull [] getReferencesByString(String text, @NotNull PsiElement position, int offsetInPosition) {
            List<PsiReference> refs = Lists.newArrayList(super.getReferencesByString(text, position, offsetInPosition));
            ValueReference vr = new ValueReference(position, getTextRange(position), context, text);
            if (!refs.isEmpty() && 0 != vr.getVariants().length) {
                refs.remove(refs.size() - 1);
                refs.add(vr);
            }
            return refs.toArray(new PsiReference[refs.size()]);
        }

        private TextRange getTextRange(PsiElement element) {
            String text = element.getText();
            int index = text.lastIndexOf(IbatisConstant.DOT_SEPARATOR);
            return -1 == index ? ElementManipulators.getValueTextRange(element) : TextRange.create(text.substring(0, index).length() + 1, text.length() - 1);
        }
    }

    private class ValueReference extends PsiReferenceBase<PsiElement> {

        private final ConvertContext context;
        private final String text;

        public ValueReference(@NotNull PsiElement element, TextRange rng, ConvertContext context, String text) {
            super(element, rng, false);
            this.context = context;
            this.text = text;
        }

        @Nullable
        @Override
        public PsiElement resolve() {
            return PropertyConverter.this.fromString(text, context);
        }

        @Override
        public Object @NotNull [] getVariants() {
            DomElement invocationElement = context.getInvocationElement();
            GroupTwo groupTwo = DomUtil.getParentOfType(invocationElement, GroupTwo.class, true);
            if (groupTwo == null) {
                Sql sql = DomUtil.getParentOfType(invocationElement, Sql.class, true);
                XmlAttributeValue xmlAttributeValue = sql.getId().getXmlAttributeValue();
                Query<PsiReference> search = ReferencesSearch.search(xmlAttributeValue, GlobalSearchScope.fileScope(getElement().getContainingFile()));
                List<PsiReference> allReference = (List<PsiReference>) search.findAll();
                if (CollectionUtils.isEmpty(allReference)) {
                    return PsiReference.EMPTY_ARRAY;
                }
                PsiElement sqlReference = allReference.get(0).getElement();
                GroupTwo parentIdDomElement = (GroupTwo) SqlMapperUtils.findParentIdDomElement(sqlReference);
                GenericAttributeValue<PsiClass> parameterClass = parentIdDomElement.getParameterClass();
                PsiClass psiClass = parameterClass.getValue();
                PsiField[] settablePsiFields = JavaUtils.findSettablePsiFields(psiClass);
                return Arrays.stream(settablePsiFields).map(PsiField::getName).toArray();
            } else {
                GenericAttributeValue<PsiClass> parameterClass = groupTwo.getParameterClass();
                PsiClass psiClass = parameterClass.getValue();
                PsiField[] settablePsiFields = JavaUtils.findSettablePsiFields(psiClass);
                return Arrays.stream(settablePsiFields).map(PsiField::getName).toArray();
            }
        }
    }

    @Override
    public @NotNull Collection<? extends PsiField> getVariants(ConvertContext convertContext) {
        return Collections.emptyList();
    }

    @Override
    public @Nullable PsiField fromString(@Nullable String value, ConvertContext context) {
        DomElement invocationElement = context.getInvocationElement();
        GroupTwo groupTwo = DomUtil.getParentOfType(invocationElement, GroupTwo.class, true);
        if (groupTwo == null) {
            Sql sql = DomUtil.getParentOfType(invocationElement, Sql.class, true);
            XmlAttributeValue xmlAttributeValue = sql.getId().getXmlAttributeValue();
            Query<PsiReference> search = ReferencesSearch.search(xmlAttributeValue, GlobalSearchScope.allScope(context.getProject()));
            List<PsiReference> allReference = (List<PsiReference>) search.findAll();
            if (CollectionUtils.isEmpty(allReference)) {
                return null;
            }
            PsiElement sqlReference = allReference.get(0).getElement();
            GroupTwo parentIdDomElement = (GroupTwo) SqlMapperUtils.findParentIdDomElement(sqlReference);
            GenericAttributeValue<PsiClass> parameterClass = parentIdDomElement.getParameterClass();
            PsiClass psiClass = parameterClass.getValue();
            return JavaUtils.findSettablePsiField(psiClass, value);
        } else {
            GenericAttributeValue<PsiClass> parameterClass = groupTwo.getParameterClass();
            PsiClass psiClass = parameterClass.getValue();
            return JavaUtils.findSettablePsiField(psiClass, value);
        }
    }

    @Override
    public @Nullable String toString(@Nullable PsiField xmlAttributeValue, ConvertContext convertContext) {
        return "ResultMapConverter.toString..........";
    }

}
