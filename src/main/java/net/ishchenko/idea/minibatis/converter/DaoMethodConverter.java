package net.ishchenko.idea.minibatis.converter;


import com.intellij.psi.PsiMethod;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.ResolvingConverter;
import net.ishchenko.idea.minibatis.model.sqlmap.SqlMap;
import net.ishchenko.idea.minibatis.util.JavaUtils;
import net.ishchenko.idea.minibatis.util.SqlMapperUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * 方法名上 ctrl + 左键 跳转xml
 * @author jinwenbiao
 * @since 2021/9/2 10:38
 */
public class DaoMethodConverter extends ResolvingConverter<PsiMethod> {
	@Override
	public @NotNull Collection<? extends PsiMethod> getVariants(ConvertContext convertContext) {
		return Collections.emptyList();
	}

	@Override
	public @Nullable PsiMethod fromString(@Nullable String id, ConvertContext convertContext) {
		SqlMap mapper = SqlMapperUtils.getMapper(convertContext.getInvocationElement());
		if (mapper == null) {
			return null;
		}
		return JavaUtils.findMethod(convertContext.getProject(), SqlMapperUtils.getNamespace(mapper), id);
	}

	@Override
	public @Nullable String toString(@Nullable PsiMethod psiMethod, ConvertContext convertContext) {
		return null;
	}

}
