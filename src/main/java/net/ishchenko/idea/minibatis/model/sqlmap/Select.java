package net.ishchenko.idea.minibatis.model.sqlmap;


import com.intellij.psi.PsiClass;
import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.Convert;
import com.intellij.util.xml.GenericAttributeValue;
import net.ishchenko.idea.minibatis.converter.AliasConverter;

/**
 * Created by IntelliJ IDEA.
 * User: Max
 * Date: 02.01.12
 * Time: 21:55
 */
public interface Select extends GroupTwo, ResultMapGroup {

    @Attribute("resultClass")
    @Convert(AliasConverter.class)
    GenericAttributeValue<PsiClass> getResultClass();


}
