from py4j.java_collections import ListConverter
from py4j.java_gateway import JavaGateway, CallbackServerParameters, GatewayParameters

from kerasNet import KerasNN


class NNInterface(object):
    def __init__(self, gateway):
        self.gateway = gateway
        self.kerasNN = KerasNN()

    def learn(self):
        print("l")
        self.kerasNN.learn()
        print("ld")

    def add(self, s, a, r, sa):
        print("a")
        self.kerasNN.add(list(s), a, r, list(sa))
        print("ad")

    def classify(self, f):
        print("c")
        s = list(f)
        answer = self.kerasNN.classify(s)
        print("cd")
        return ListConverter().convert(answer, gateway._gateway_client)

    class Java:
        implements = ["net.demilich.metastone.game.behaviour.diplom.pythonBridge.NNInterface"]


if __name__ == "__main__":
    gateway = JavaGateway(
        callback_server_parameters=CallbackServerParameters(), gateway_parameters=GatewayParameters(port=1234))
    interface = NNInterface(gateway)
    gateway.entry_point.registerPython(interface)
    gateway.entry_point.print()
