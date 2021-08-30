package net.ishchenko.idea.minibatis.util;


import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import net.ishchenko.idea.minibatis.model.sqlmap.SqlMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * @author jiwenbiao
 * @since : 2021/4/28 18:09
 */
public class SqlMapperUtils {
    public static SqlMap getMapperByDomElement(DomElement invocationElement) {
        SqlMap sqlMap = DomUtil.getParentOfType(invocationElement, SqlMap.class, true);
        if (sqlMap == null) {
            throw new IllegalArgumentException("Unknown element");
        }
        return sqlMap;
    }

    public static IdDomElement findParentIdDomElement(@Nullable PsiElement element) {
        DomElement domElement = DomUtil.getDomElement(element);
        if (null == domElement) {
            return null;
        }
        if (domElement instanceof IdDomElement) {
            return (IdDomElement) domElement;
        }
        return DomUtil.getParentOfType(domElement, IdDomElement.class, true);
    }

    public static boolean isElementWithinMybatisFile(@NotNull PsiElement element) {
        PsiFile psiFile = element.getContainingFile();
        return element instanceof XmlElement && DomUtils.isIbatisFile(psiFile);
    }

    public static Collection<SqlMap> findMappers(@NotNull Project project) {
        return DomUtils.findDomElements(project, SqlMap.class);
    }
    public static <T extends IdDomElement> String getIdSignature(@NotNull T domElement) {
        SqlMap parentOfType = DomUtil.getParentOfType(domElement, SqlMap.class, true);
        if (parentOfType == null) {
            return null;
        }
        return getNamespace(parentOfType) + "." + getId(domElement);
    }

    public static <T extends IdDomElement> String getId(@NotNull T domElement) {
        return domElement.getId().getRawText();
    }

    public static String getNamespace(@NotNull SqlMap mapper) {
        String ns = mapper.getNamespace().getStringValue();
        return null == ns ? "" : ns;
    }

    public static String getNamespace(@NotNull DomElement element) {
        SqlMap mapper = getMapper(element);
        if (mapper == null) {
            return null;
        }
        return getNamespace(mapper);
    }

    public static SqlMap getMapper(@NotNull DomElement element) {
        SqlMap parentOfType = DomUtil.getParentOfType(element, SqlMap.class, true);

        if (parentOfType != null) {
            return parentOfType;
        }
        return null;
    }

}
