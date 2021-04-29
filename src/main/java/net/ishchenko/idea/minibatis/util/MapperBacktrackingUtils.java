package net.ishchenko.idea.minibatis.util;


import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;

/**
 * @author jiwenbiao
 * @since : 2021/4/29 14:10
 */
public class MapperBacktrackingUtils {

    public static PsiClass getPropertyClazz(XmlAttributeValue element) {
        DomElement domElement = DomUtil.getDomElement(element);
        if (domElement == null) {
            return null;
        }

        return null;
    }

}
