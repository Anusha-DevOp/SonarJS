/*
 * SonarQube JavaScript Plugin
 * Copyright (C) 2011 SonarSource and Eriks Nukis
 * sonarqube@googlegroups.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.javascript.tree.impl.declaration;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import org.sonar.javascript.tree.impl.JavaScriptTree;
import org.sonar.javascript.tree.impl.lexical.InternalSyntaxToken;
import org.sonar.javascript.tree.impl.statement.BlockTreeImpl;
import org.sonar.plugins.javascript.api.tree.Tree;
import org.sonar.plugins.javascript.api.tree.declaration.AccessorMethodDeclarationTree;
import org.sonar.plugins.javascript.api.tree.declaration.GeneratorMethodDeclarationTree;
import org.sonar.plugins.javascript.api.tree.declaration.ParameterListTree;
import org.sonar.plugins.javascript.api.tree.expression.ExpressionTree;
import org.sonar.plugins.javascript.api.tree.lexical.SyntaxToken;
import org.sonar.plugins.javascript.api.visitors.TreeVisitor;

import javax.annotation.Nullable;
import java.util.Iterator;

public class MethodDeclarationTreeImpl extends JavaScriptTree implements GeneratorMethodDeclarationTree, AccessorMethodDeclarationTree {

  private final Kind kind;
  @Nullable
  private InternalSyntaxToken staticToken;
  @Nullable
  private final InternalSyntaxToken starToken;
  @Nullable
  private final InternalSyntaxToken accessorToken;
  private final ExpressionTree name;
  private final ParameterListTreeImpl parameters;
  private final BlockTreeImpl body;

  private MethodDeclarationTreeImpl(
    Kind kind,
    @Nullable InternalSyntaxToken starToken, @Nullable InternalSyntaxToken accessorToken,
    ExpressionTree name,
    ParameterListTreeImpl parameters,
    BlockTreeImpl body) {

    this.kind = kind;
    this.starToken = starToken;
    this.accessorToken = accessorToken;
    this.name = name;
    this.parameters = parameters;
    this.body = body;
  }

  public static MethodDeclarationTreeImpl newMethodOrGenerator(@Nullable InternalSyntaxToken starToken, ExpressionTree name, ParameterListTreeImpl parameters, BlockTreeImpl body) {
    return new MethodDeclarationTreeImpl(starToken == null ? Kind.METHOD : Kind.GENERATOR_METHOD, starToken, null, name, parameters, body);
  }

  public static MethodDeclarationTreeImpl newAccessor(InternalSyntaxToken accessorToken, ExpressionTree name, ParameterListTreeImpl parameters, BlockTreeImpl body) {
    return new MethodDeclarationTreeImpl("get".equals(accessorToken.text()) ? Kind.GET_METHOD : Kind.SET_METHOD, null, accessorToken, name, parameters, body);
  }

  public MethodDeclarationTreeImpl completeWithStaticToken(InternalSyntaxToken staticToken) {
    this.staticToken = staticToken;

    return this;
  }

  @Nullable
  @Override
  public SyntaxToken staticToken() {
    return staticToken;
  }

  @Override
  public SyntaxToken starToken() {
    Preconditions.checkState(this.is(Kind.GENERATOR_METHOD));
    return starToken;
  }

  @Override
  public InternalSyntaxToken accessorToken() {
    Preconditions.checkState(this.is(Kind.GET_METHOD) || this.is(Kind.SET_METHOD));
    return accessorToken;
  }

  @Override
  public ExpressionTree name() {
    return name;
  }

  @Override
  public ParameterListTree parameters() {
    return parameters;
  }

  @Override
  public BlockTreeImpl body() {
    return body;
  }

  @Override
  public Kind getKind() {
    return kind;
  }

  @Override
  public Iterator<Tree> childrenIterator() {
    return Iterators.forArray(staticToken, starToken, accessorToken, name, parameters, body);
  }

  @Override
  public void accept(TreeVisitor visitor) {
    visitor.visitMethodDeclaration(this);
  }
}