package net.ishchenko.idea.minibatis.converter;


import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomJavaUtil;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.PsiClassConverter;
import com.intellij.util.xml.ResolvingConverter;
import net.ishchenko.idea.minibatis.alias.AliasFacade;
import net.ishchenko.idea.minibatis.util.IbatisConstant;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * @author yanglin
 */
public class AliasConverter extends ResolvingConverter<PsiClass> implements CustomReferenceConverter<PsiClass> {

    private final PsiClassConverter delegate = new PsiClassConverter();

    @Nullable
    @Override
    public PsiClass fromString(@Nullable @NonNls String s, ConvertContext context) {
        if (StringUtil.isEmptyOrSpaces(s)) {
            return null;
        }
        if (!s.contains(IbatisConstant.DOT_SEPARATOR)) {
            return AliasFacade.getInstance(context.getProject()).findPsiClass(context.getXmlElement(), s);
        }
        return DomJavaUtil.findClass(s.trim(), context.getFile(), context.getModule(), GlobalSearchScope.allScope(context.getProject()));
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
            return new PsiReference[]{};
        }
    }

    @Override
    public @NotNull Collection<? extends PsiClass> getVariants(ConvertContext convertContext) {
        return Collections.emptyList();
    }

}
