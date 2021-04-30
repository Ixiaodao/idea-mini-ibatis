package net.ishchenko.idea.minibatis.referance;


import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.xml.GenericAttributeValue;
import net.ishchenko.idea.minibatis.model.sqlmap.GroupTwo;
import net.ishchenko.idea.minibatis.util.JavaUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author yanglin
 */
public class ContextPsiFieldReference extends PsiReferenceBase<XmlAttributeValue> {

    protected GroupTwo groupTwo;
    protected int index;

    public ContextPsiFieldReference(XmlAttributeValue element, TextRange range, int index) {
        super(element, range, false);
        this.index = index;
    }

    public ContextPsiFieldReference(XmlAttributeValue element, TextRange range, int index, GroupTwo groupTwo) {
        super(element, range, false);
        this.index = index;
        this.groupTwo = groupTwo;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        GenericAttributeValue<PsiClass> parameterClass = groupTwo.getParameterClass();
        if (parameterClass.getValue() == null) {
            return null;
        }
        return JavaUtils.findSettablePsiField(parameterClass.getValue(), getElement().getValue());
    }

    @Override
    public Object @NotNull [] getVariants() {
        GenericAttributeValue<PsiClass> resultClass = groupTwo.getParameterClass();
        return JavaUtils.findSettablePsiFields(resultClass.getValue());
    }



}
