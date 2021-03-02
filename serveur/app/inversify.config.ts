import { Container } from "inversify";
import { Application } from "./app";
import { PresentationController } from "./controllers/presentation.controller";
import { SocketController } from "./controllers/sockets.controller";
import { Server } from "./server";
import { AccountService } from "./services/account.service";
import { ChatService } from "./services/chat.service";
import { DatabaseService } from "./services/database.service";
import { GameCreationService } from "./services/gameCreation.service";
import { LiveGameService } from "./services/liveGame.service";
import { LoginService } from "./services/login.service";
import { PartyService } from "./services/party.service";
import { PresentationService } from "./services/presentation.service";
import Types from "./types";

const container: Container = new Container();

container.bind(Types.Server).to(Server);
container.bind(Types.Application).to(Application);

container.bind(Types.PresentationController).to(PresentationController);
container.bind(Types.SocketController).to(SocketController);

container.bind(Types.AccountService).to(AccountService);
container.bind(Types.ChatService).to(ChatService);
container.bind(Types.DatabaseService).to(DatabaseService);
container.bind(Types.PresentationService).to(PresentationService);
container.bind(Types.LoginService).to(LoginService);
container.bind(Types.GameCreationService).to(GameCreationService);

container.bind(Types.PartyService).to(PartyService);
container.bind(Types.LiveGameService).to(LiveGameService);

export { container };
