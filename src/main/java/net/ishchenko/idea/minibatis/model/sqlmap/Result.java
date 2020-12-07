package net.ishchenko.idea.minibatis.model.sqlmap;


import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.Referencing;
import net.ishchenko.idea.minibatis.ResultMapReference;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 10.04.12
 * Time: 22:53
 */
public interface Result extends DomElement {

    @NameValue
    @Attribute("property")
    GenericAttributeValue<String> getProperty();

    @Attribute("typeHandler")
    GenericAttributeValue<TypeAlias> getTypeHandler();

    @Attribute("resultMap")
    @Referencing(ResultMapReferenceConverter.class)
    GenericAttributeValue<ResultMap> getResultMap();

    class ResultMapReferenceConverter implements CustomReferenceConverter {

        @NotNull
        @Override
        public PsiReference[] createReferences(GenericDomValue genericDomValue, PsiElement element, ConvertContext context) {
            return new PsiReference[]{new ResultMapReference(element)};
        }

    }

}
