package net.ishchenko.idea.minibatis.converter;


import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.ResolvingConverter;
import net.ishchenko.idea.minibatis.model.sqlmap.GroupTwo;
import net.ishchenko.idea.minibatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author yanglin
 */
public class PropertyConverter extends ResolvingConverter<PsiField> {
    public PropertyConverter() {
    }

    @Override
    public @NotNull Collection<? extends PsiField> getVariants(ConvertContext context) {
        DomElement invocationElement = context.getInvocationElement();
        GroupTwo groupTwo = DomUtil.getParentOfType(invocationElement, GroupTwo.class, true);
        if (groupTwo == null) {
            return Collections.emptyList();
        } else {
            GenericAttributeValue<PsiClass> parameterClass = groupTwo.getParameterClass();
            PsiClass psiClass = parameterClass.getValue();
            PsiField[] settablePsiFields = JavaUtils.findSettablePsiFields(psiClass);
            return Arrays.asList(settablePsiFields);
        }

    }

    @Override
    public @Nullable PsiField fromString(@Nullable String value, ConvertContext context) {
        DomElement invocationElement = context.getInvocationElement();
        GroupTwo groupTwo = DomUtil.getParentOfType(invocationElement, GroupTwo.class, true);
        if (groupTwo == null) {
            return null;
        } else {
            GenericAttributeValue<PsiClass> parameterClass = groupTwo.getParameterClass();
            PsiClass psiClass = parameterClass.getValue();
            return JavaUtils.findSettablePsiField(psiClass, value);
        }
    }

    @Override
    public @Nullable String toString(@Nullable PsiField psiField, ConvertContext convertContext) {
        if (psiField == null) {
            return "";
        }
        return psiField.getName();
    }

}
