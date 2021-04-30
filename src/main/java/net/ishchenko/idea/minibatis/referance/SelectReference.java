package net.ishchenko.idea.minibatis.referance;


import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.ReferenceSetBase;
import com.intellij.psi.xml.XmlAttributeValue;
import net.ishchenko.idea.minibatis.model.sqlmap.GroupTwo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author jiwenbiao
 * @since : 2021/4/29 14:27
 */
public class SelectReference extends ReferenceSetBase<PsiReference> {
    private final GroupTwo groupTwo;
    public SelectReference(String text, @NotNull PsiElement element, int offset, GroupTwo groupTwo) {
        super(text, element, offset, DOT_SEPARATOR);
        this.groupTwo = groupTwo;
        System.out.println("11111");

    }

    @Override
    protected @Nullable PsiReference createReference(TextRange range, int index) {
        System.out.println("22222");
        XmlAttributeValue element = (XmlAttributeValue) getElement();
        return null == element ? null : new ContextPsiFieldReference(element, range, index, groupTwo);
    }

}
