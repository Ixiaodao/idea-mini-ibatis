package net.ishchenko.idea.minibatis.util;


import com.google.common.base.Optional;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationMemberValue;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiParameter;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.util.PsiTreeUtil;
import net.ishchenko.idea.minibatis.annotation.Annotation;
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author yanglin
 */
public final class JavaUtils {

    private JavaUtils() {
        throw new UnsupportedOperationException();
    }

    public static boolean isElementWithinInterface(@Nullable PsiElement element) {
        if (element instanceof PsiClass && ((PsiClass) element).isInterface()) {
            return true;
        }
        PsiClass type = PsiTreeUtil.getParentOfType(element, PsiClass.class);
        return type != null && type.isInterface();
    }

    public static boolean isModelClazz(@Nullable PsiClass clazz) {
        return null != clazz && !clazz.isAnnotationType() && !clazz.isInterface() && !clazz.isEnum() && clazz.isValid();
    }
    public static String getAnnotationValueText(PsiClass psiClass, String alias) {
        if (!isAnnotationPresent(psiClass, alias)) {
            return null;
        }
        PsiAnnotation psiAnnotation = getPsiAnnotation(psiClass, alias);
        return psiAnnotation != null ? psiAnnotation.findAttributeValue("alias").getText().replaceAll("\"", "") : null;
    }

    public static PsiAnnotation getPsiAnnotation(@NotNull PsiModifierListOwner target, String aliasName) {
        PsiModifierList modifierList = target.getModifierList();
        return null == modifierList ? null : modifierList.findAnnotation(aliasName);
    }

    private static boolean isAnnotationPresent(PsiClass psiClass, String alias) {
        PsiModifierList modifierList = psiClass.getModifierList();
        return null != modifierList && null != modifierList.findAnnotation(alias);
    }

    public static PsiClass findClazz(@NotNull Project project, @NotNull String clazzName) {
        return JavaPsiFacade.getInstance(project).findClass(clazzName, GlobalSearchScope.allScope(project));
    }

    public static PsiField[] findSettablePsiFields(final PsiClass clazz) {
        if (clazz == null) {
            return PsiField.EMPTY_ARRAY;
        }
        final PsiField[] fields = clazz.getAllFields();
        final List<PsiField> settableFields = new ArrayList<>(fields.length);

        for (final PsiField f : fields) {
            final PsiModifierList modifiers = f.getModifierList();

            if (modifiers != null && (
                    modifiers.hasModifierProperty(PsiModifier.STATIC) ||
                            modifiers.hasModifierProperty(PsiModifier.FINAL))) {
                continue;
            }

            settableFields.add(f);
        }

        return settableFields.toArray(new PsiField[0]);
    }

    public static PsiField findSettablePsiField(final PsiClass clazz, final String propertyName) {
        if (clazz == null) {
            return null;
        }
        return PropertyUtil.findPropertyField(clazz, propertyName, false);
    }

    public static PsiMethod findMethod(@NotNull Project project, @NotNull IdDomElement element) {
        return findMethod(project, SqlMapperUtils.getNamespace(element), SqlMapperUtils.getId(element));
    }

    public static PsiMethod findMethod(@NotNull Project project, @Nullable String clazzName, @Nullable String methodName) {
        if (StringUtils.isBlank(clazzName) && StringUtils.isBlank(methodName)) {
            return null;
        }

        PsiClass clazz = findClazz(project, clazzName);
        if (clazz != null) {
            PsiMethod[] methods = clazz.findMethodsByName(methodName, true);
            return ArrayUtils.isEmpty(methods) ? null : methods[0];
        }
        return null;
    }
}
