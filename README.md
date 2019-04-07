# rabbitMQ
Configurações:
Instale o rabbitMQ. https://www.rabbitmq.com/download.html
Instale o Erlang (ao instalar o RabbitMQ, uma opção de instalação de Erlang aparecerá. Caso não ocorra acesse https://www.erlang.org/downloads)
Baixe o projeto

Execução:
1 - Classe "SenderBroker". Responsável por executar operações de compra, venda e info.
    Entradas: host
              Ação(compra): quantidade, valor, corretora
              Ação(venda): quantidade, valor, corretora
              Ação(info): data
2 - Classe "BolsaTransaction". Responsável por fazer os cálculos de venda e compra e informar aos brokers suas ações.
    Entradas: host
3 - Classe "ReceiverBroker". Responsável por receber as operações realizadas na bolsa de valores.
    Entradas: host

Obs.: as classes não precisam estar rodando ao mesmo tempo para que as operações ocorram.
