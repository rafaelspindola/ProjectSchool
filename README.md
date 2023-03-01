# ProjectSchoolAlura

Essa é a solução que encontrei para os problemas do desafio técnico da Alura. Os requisitos estão detalhados nesse arquivo: 

https://github.com/rafaelspindola/ProjectSchoolAlura/blob/main/requisitos.md

## Swagger

http://localhost:8080/swagger-ui/index.html#/

API como um todo: 

![swagger 1](https://user-images.githubusercontent.com/108681887/222069273-6bd80237-b9f8-4f3a-a6d5-ab464de73a81.PNG)

É possível criar um novo usuário e achá-lo pelo seu nome de usuário. Essas funcionalidades já vieram implementadas.

![swagger 2](https://user-images.githubusercontent.com/108681887/222069591-62f90579-9024-4c3f-89e2-6d3c1e464751.PNG)

É possível procurar por todos os cursos, por um curso específico usando seu código, criar um curso, gerar uma matrícula e um relatório de matrículas.
A função GET `/courses` foi corrigida por causa dos erros nos testes. 
Já a matrícula POST `/courses/{code}/enroll` e o relatório de matrículas GET `/courses/enroll/report` foram funcionalidades implementadas no desafio.

![swagger 3](https://user-images.githubusercontent.com/108681887/222069752-451714e8-8c3f-459c-bd7d-2a8f76cfd9bc.PNG)


## 1 - Correção dos testes automatizados
Esse teste estava com problemas e gerando o seguinte erro:

![test error photo](https://user-images.githubusercontent.com/108681887/222059529-4dd269ca-d6e5-42cd-b6d7-010c3a5b3c10.PNG)

Inicialmente eu achei que era um problema com o teste em si, 
mas depois de fazer algumas tentativas de correção logo vi que o problema estava no método allCourses() no CourseController.
Esse era o método com problemas:

![método inicial](https://user-images.githubusercontent.com/108681887/222059992-2006885c-7077-4a6c-a4fe-664d14e04622.PNG)

Diferentemente do que o erro sugeria, o problema não estava na falta de definição do conteúdo, mas sim numa requisição com retorno errado.
Essa foi a solução encontrada:

![solução 1](https://user-images.githubusercontent.com/108681887/222060269-794ee2cd-a57b-4b06-b2f3-1ae65467a89a.PNG)

A primeira linha requere ao banco de dados que retorne uma lista de todos os cursos disponíveis.
Logo após isso, cada objeto de curso é transformado num objeto de resposta de cursos, associando ambas as coisas e criando uma lista de cursos como resposta.
O retorno é então caracterizado por uma lista de cursos disponíveis e status 200 OK.

## 2 - Implementar matrícula de usuário
A matrícula de um indivíduo em um curso sugere uma relação "many to many" entre as entidades de usuário e curso com uma coluna extra para registrar a data do processo.
Sendo assim foi feita a criação de uma classe para representar a chave primária composta de usuários e cursos (EnrollmentId), uma classe que representasse o objeto da matrícula (Enrollment),
uma classe para solicitar uma matrícula (NewEnrollmentRequest), além de alterar as classes User e Course. 
Esse foi o método criado no CourseController com endpoint `/courses/{courseCode}/enroll`:

![solução 2](https://user-images.githubusercontent.com/108681887/222062514-6b48ff6a-20ee-42c2-8820-85521a7e57e9.PNG)

A primeira linha procura se um usuário existe pelo seu nome de usuário e, caso não exista, retorna status 404 not found.
Depois um curso é procurado pelo seu código e, caso não exista, retorna um status 404 not found.
Então é checado se tal usuário que faz o requerimento de matrícula já não está matriculado no curso. Se estiver, a rematrícula é impedida e retorna o status 400 bad request.
Se tudo ocorrer bem, o usuário é adicionado à lista de alunos matriculados, a matrícula é guardada no banco de dados e um status 201 created é retornado.

**P.S: o entityManager.clear() foi necessário porque, sem ele, um status 500 era retornado e o código de erro sugestionava que haviam duas entidades sendo associadas ao mesmo registro.
Tendo em vista que provavelmente é um problema de cache no banco de dados em memória, o método clear() foi utilizado para resolver o problema.**

### 2.1 - Testes para a funcionalidade de matrícula

Dez testes foram adicionados para essa funcionalidade. Foi criado um EnrollmentRepository para ajudar no processo de testagem.

**O primeiro testa a matrícula de um indivíduo em um curso:**

![teste 1](https://user-images.githubusercontent.com/108681887/222063902-68aefe72-0a8f-4c38-9895-a8a39438a8d6.PNG)

**O segundo testa a matrícula de um indivíduo em vários cursos:**

![teste 2](https://user-images.githubusercontent.com/108681887/222064063-9b7b4084-6474-4928-89c2-f027f6b35394.PNG)

**O terceiro testa a exceção not found 404 por não achar um estudante válido a ser matriculado:**

![teste 3](https://user-images.githubusercontent.com/108681887/222064159-58b8dca1-3ce4-454b-8605-9a797895edad.PNG)

**O quarto testa a exceção 404 not found por não achar um curso valido para que o estudante se matricule:** 

![teste 4](https://user-images.githubusercontent.com/108681887/222064525-b5592f11-245c-4ed4-a551-c63514b256a8.PNG)

**O quinto testa o impedimento da matrícula pelo estudante já estar matriculado:**

![teste 5](https://user-images.githubusercontent.com/108681887/222064703-0849ad6c-6036-4852-84e0-520650ff67aa.PNG)

**O sexto retorna exceção 404 quando o curso não existe:**

![teste 9](https://user-images.githubusercontent.com/108681887/222182328-200497e6-0657-4031-954e-eb4830a61d4b.PNG)

**O sétimo retorna exceção 204 quando não há lista de cursos no banco de dados:**

![teste 10](https://user-images.githubusercontent.com/108681887/222182772-c24e5854-83e5-4180-8cd2-11ef481e4678.PNG)

**O oitavo testa exceções relacionadas a validações como @NotBlack e limite de caracteres de input:**

![teste 11](https://user-images.githubusercontent.com/108681887/222183198-77970bab-108e-4d33-b569-9182cf5999e9.PNG)

**O nono impede a duplicação de cursos com código duplo:** 

![teste12](https://user-images.githubusercontent.com/108681887/222183581-da369db8-f31f-4873-bee1-a778eab746c9.PNG)

**O décimo impede a duplicação de cursos com nome duplo:** 

![teste 13](https://user-images.githubusercontent.com/108681887/222183758-a715f16e-4308-4c48-9eb1-f27831a9ee27.PNG)

## 3 - Implementar relatório de matrículas

Relacionado ao relatório de matrículas em que os usuários têm ao menos uma matrícula e são ordenados em ordem decrescente do número de matrículas,
foi criado um método HTTP GET no endpoint `/courses/enroll/report` e uma classe "EnrollmentReport" para retornar o JSON requerido.

![solução 3](https://user-images.githubusercontent.com/108681887/222065712-9d4fc56c-18ed-49fc-a96b-8929e39875a1.PNG)

Primeiramente é realizada a procura por todos os usuários com ao menos uma matrícula em curso.
Após isso é criada uma sequência de objetos (stream) e cada usuário é associado (map) a um objeto do relatório pelo número de matrículas e seu email.
A partir daí esses objetos são ordenados segundo seu valor de quantidade de matrículas em ordem decrescente e é criada uma lista desses objetos. 
O operador ternário é utilizado para tornar o código mais limpo, retornando status 204 no content caso não haja um relatório a ser produzido.
Se houver, o retorno é status 200 ok.

### 3.1 - Testes para a funcionalidade de relatório

Dois testes foram implementados para a nova funcionalidade. 

**O primeiro testa a criação de um relatório:**

![teste 6](https://user-images.githubusercontent.com/108681887/222066927-f0bdb6a8-8333-4b4a-8a3c-fbb9911687f9.PNG)

**O segundo testa a ausência de relatório e uma exceção status 204 no content:**

![teste 7](https://user-images.githubusercontent.com/108681887/222067204-2371faa3-7033-403e-8d6d-3fbfa1adcc8e.PNG)

**P.S: alguns testes não estavam passando na bateria como um todo, apesar de estarem funcionando sozinhos.
Provavelmente os outros testes estavam alterando os dados utilizados em alguns testes e,
para resolver esse problema, foi implementado um clearDatabase() após cada teste realizado.**

![teste 8](https://user-images.githubusercontent.com/108681887/222067939-d770151e-7828-4162-989c-d4c82c096c93.PNG)

## Observações

1 - o logger foi implementado adicionalmente para verificação de bugs e erros em código.

2 - os testes realizados em todo o projeto cobrem 68% dos métodos e 76% das linhas de código.

3 - o swagger também foi implementado adicionalmente como uma ferramenta de documentação.
