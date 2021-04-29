package net.ishchenko.idea.minibatis.util;


import com.intellij.psi.PsiElement;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import net.ishchenko.idea.minibatis.model.sqlmap.IdDomElement;
import net.ishchenko.idea.minibatis.model.sqlmap.SqlMap;
import org.jetbrains.annotations.Nullable;

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

}
