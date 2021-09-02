package net.ishchenko.idea.minibatis.model.sqlmap;


import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import net.ishchenko.idea.minibatis.converter.AliasConverter;
import net.ishchenko.idea.minibatis.converter.DaoMethodConverter;
import net.ishchenko.idea.minibatis.converter.ParameterMapConverter;
import org.jetbrains.annotations.NotNull;

/**
 * @author jiwenbiao
 * @since : 2021/4/28 18:58
 */
public interface GroupTwo extends GroupOne, IdDomElement {

    @NotNull
    @Convert(AliasConverter.class)
    @Attribute("parameterClass")
    public GenericAttributeValue<PsiClass> getParameterClass();

    @NotNull
    @Attribute("parameterMap")
    @Convert(ParameterMapConverter.class)
    public GenericAttributeValue<XmlTag> getParameterMap();

    @Attribute("id")
    @Convert(DaoMethodConverter.class)
    public GenericAttributeValue<String> getId();
}
