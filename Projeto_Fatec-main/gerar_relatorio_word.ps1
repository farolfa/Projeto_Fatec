$outputPath = "c:\Users\paimn\Desktop\trabalho fatec\Projeto_Fatec-main\Relatorio_Projeto_Extensao_INFO4P_2026-1_Preenchido.docx"

$conteudo = @(
"RELATORIO PROJETO EXTENSAO - INFO 4P 2026-1",
"",
"1. IDENTIFICACAO",
"Aluno 1: Thadeu Paramo",
"Aluno 2: Raul Gonsalves",
"Curso/Turma: INFO 4P - 2026-1",
"Instituicao: FATEC",
"",
"2. TITULO DO PROJETO",
"Plataforma FazTudoJa - Ecossistema Web para Conexao entre Clientes e Prestadores de Servico",
"",
"3. RESUMO EXECUTIVO",
"O projeto FazTudoJa e uma plataforma digital de intermediacao de servicos, desenvolvida para conectar clientes e prestadores de forma eficiente, segura e rastreavel. O sistema contempla funcionalidades de cadastro, solicitacao de servicos, recebimento e comparacao de propostas, mensagens, notificacoes, avaliacao dos atendimentos e area administrativa para governanca da operacao.",
"",
"4. PROBLEMA E JUSTIFICATIVA",
"O mercado de servicos locais apresenta desafios de confianca, padronizacao de atendimento e dificuldade para comparar propostas. A solucao proposta reduz assimetrias de informacao e oferece um fluxo estruturado de contratacao, elevando transparencia, experiencia do usuario e controle operacional.",
"",
"5. OBJETIVO GERAL",
"Construir uma aplicacao web completa para gerenciamento de demandas de servico, promovendo conexao qualificada entre contratantes e profissionais, com suporte a acompanhamento ponta a ponta do ciclo de atendimento.",
"",
"6. OBJETIVOS ESPECIFICOS",
"- Permitir cadastro e autenticacao de usuarios por perfil.",
"- Registrar pedidos de servico com detalhes operacionais.",
"- Disponibilizar propostas e historico de negociacao.",
"- Habilitar comunicacao via mensagens e notificacoes.",
"- Implementar avaliacao de prestadores e controle de favoritos.",
"- Oferecer area administrativa para monitoramento de usuarios, pedidos, categorias e avaliacoes.",
"",
"7. ESCOPO FUNCIONAL DO SISTEMA",
"Frontend (React + Vite):",
"- Paginas publicas: Home, Acesso, Solicitar Servico, Buscar Prestadores, FAQ, Centro de Ajuda, Projeto Ambiental e Perfil Publico de Prestador.",
"- Area Cliente: Dashboard, Meus Pedidos, Orcamentos, Mensagens, Notificacoes, Avaliacoes, Tickets, Favoritos, Historico e Perfil.",
"- Area Prestador: Dashboard, Servicos Disponiveis, Minhas Propostas, Historico, Mensagens, Notificacoes, Avaliacoes e Perfil.",
"- Area Admin: Dashboard, Usuarios, Pedidos, Avaliacoes e Categorias.",
"",
"Backend (Spring Boot):",
"- API REST para dominio de negocio com controladores, servicos, repositorios e entidades.",
"- Modulos centrais: Usuario, Cliente, Prestador, Pedido, Proposta, Oferta, Contrato, Pagamento, Mensagem, Notificacao, Ticket de Suporte, Categoria, ServicoCatalogo, ServicoOferecido, Agenda e Favorito.",
"- Persistencia com Spring Data JPA e suporte a bancos H2, PostgreSQL e SQL Server.",
"",
"8. ARQUITETURA E TECNOLOGIAS",
"Camada de apresentacao: React 18 + Vite 6, roteamento com React Router, componentes UI com bibliotecas modernas (MUI, Radix, Tailwind).",
"Camada de negocio: Java 21 + Spring Boot 3.5, padrao em camadas (Controller/Service/Repository).",
"Persistencia: JPA/Hibernate e drivers para multiplos SGBDs.",
"",
"9. RESULTADOS ALCANCADOS",
"- Estrutura completa de frontend e backend com organizacao por dominios.",
"- Cobertura de casos reais de marketplace de servicos.",
"- Separacao de papeis de acesso (cliente, prestador e administrador).",
"- Base escalavel para evolucao de seguranca, observabilidade e CI/CD.",
"",
"10. DIFICULDADES E APRENDIZADOS",
"Durante o desenvolvimento, os principais desafios envolveram modelagem de dominio, sincronizacao entre fluxo de telas e regras de API e padronizacao de dados entre perfis de usuario. Como aprendizado, destaca-se a importancia de contratos de API bem definidos, validacoes consistentes e rastreabilidade de eventos do ciclo de atendimento.",
"",
"11. MELHORIAS PROPOSTAS",
"- Implementar autenticacao robusta com JWT e politicas de autorizacao refinadas.",
"- Adicionar testes automatizados (unitarios, integracao e E2E) com pipeline CI/CD.",
"- Integrar observabilidade (logs estruturados, metricas e tracing).",
"- Evoluir modulos de pagamento e antifraude.",
"- Publicar documentacao da API com OpenAPI/Swagger.",
"",
"12. CONCLUSAO",
"O projeto atende ao objetivo de oferecer uma plataforma de servicos moderna, com arquitetura consistente e funcionalidades aderentes a um cenario real de negocio. A solucao demonstra maturidade tecnica para continuidade academica e evolucao para contexto de producao.",
"",
"13. ASSINATURAS",
"Aluno 1: Thadeu Paramo",
"Aluno 2: Raul Gonsalves",
"Data: 20/04/2026"
)

try {
    $word = New-Object -ComObject Word.Application
    $word.Visible = $false
    $doc = $word.Documents.Add()

    foreach ($linha in $conteudo) {
        $range = $doc.Content
        $range.Collapse(0)
        [void]$range.InsertAfter($linha)
        [void]$range.InsertParagraphAfter()
    }

    $doc.SaveAs([ref]$outputPath, [ref]16)
    $doc.Close()
    $word.Quit()

    Write-Output "OK: Documento gerado em $outputPath"
}
catch {
    if ($doc -ne $null) { $doc.Close() }
    if ($word -ne $null) { $word.Quit() }
    Write-Error "Falha ao gerar .docx via Word COM: $($_.Exception.Message)"
    exit 1
}
